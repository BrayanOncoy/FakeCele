package pe.facele.michell.task;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import pe.facele.docele.schemas.v11.comprobantevinculado.ComprobanteReversionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.ComprobanteVinculado;
import pe.facele.docele.schemas.v11.comprobantevinculado.DomicilioFiscalType;
import pe.facele.docele.schemas.v11.comprobantevinculado.EmisorType;
import pe.facele.docele.schemas.v11.comprobantevinculado.IdReversionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.ObjectFactory;
import pe.facele.docele.schemas.v11.comprobantevinculado.ResumenDiarioReversionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.TipoComprobanteVinculadoType;
import pe.facele.docele.dol.services.emisionservice.Declarar;
import pe.facele.docele.dol.services.emisionservice.DeclararResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoDeclararType;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.FormatoFecha;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.Resource;
import pe.facele.michell.api.WaterMark;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.RetencionBEAN;
import pe.facele.michell.bean.SerieBEAN;

public class RunnableEmisionRetencionReversion implements Runnable {
	Logger logger = Logger.getLogger(this.getClass());

	private DAO dao;

	private Integer organizacionID;

	public RunnableEmisionRetencionReversion(Integer organizacionID) {
		this.organizacionID = organizacionID;
	}

	@Override
	public void run() {
		logger.info("Reversando Retenciones");
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);
			
			List<Date> porAnular = dao.getComprobantesReversionFecha(organizacionID, Constantes.MICHELL_ESTADO_RETENCION_REVERSION.POREMITIR.getCode(), Constantes.MICHELL_ESTADO_RETENCION.APROBADO.getCode());
			for (Date fecha : porAnular) {
				generaReversion(fecha);
				con.commit();
			}
		} catch (Exception e) {
			logger.error(e, e);
			try {con.rollback();} catch (SQLException e1) {e1.printStackTrace();}
		} finally {
			DataSourceFactory.desconectar(con);
		}
	}

	private void generaReversion(Date fecha) {
		logger.info("Procesando reversiones para fecha: " + fecha.toString());
		try {
			List<IdProceso> comprobantes = dao.getComprobantesReversion(organizacionID, fecha, Constantes.MICHELL_ESTADO_RETENCION_REVERSION.ACTIVO.getCode(), Constantes.MICHELL_ESTADO_RETENCION.APROBADO.getCode());
			
			if (comprobantes.size() == 0)
				logger.warn("Porque se está generando una reversion para Fecha que no tiene registros?");
			
			SerieBEAN serie = dao.getSerie(comprobantes.get(0));
			
			OrganizacionBEAN organizacion = dao.getOrganizacion(serie.getIdOrganizacion());
			
			ObjectFactory of = new ObjectFactory();
			
			EmisorType emisorType = of.createEmisorType();
			emisorType.setRuc(new BigInteger(organizacion.getRuc()));
			emisorType.setRazonSocial(organizacion.getDescripcion());
			emisorType.setNombreComercial(organizacion.getDescripcion());
			
			DomicilioFiscalType domicilioFiscal = of.createDomicilioFiscalType();
			domicilioFiscal.setUbigeo(BigInteger.valueOf(Resource.getUBIGEO(serie.getIdOrganizacion())));
			domicilioFiscal.setDireccion(organizacion.getDireccion());
			domicilioFiscal.setUrbanizacion(organizacion.getDistrito());
			domicilioFiscal.setDistrito(organizacion.getDistrito());
			domicilioFiscal.setProvincia(organizacion.getProvincia());
			domicilioFiscal.setDepartamento(organizacion.getDistrito());
			emisorType.setDomicilio(domicilioFiscal);
			
			IdReversionType reversionIDType = of.createIdReversionType();
			reversionIDType.setFechaComunicacion(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(new Date())));
			reversionIDType.setFechaComprobantes(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(comprobantes.get(0).getFechaEmision())));
			reversionIDType.setTipoComprobante(TipoComprobanteVinculadoType.RETENCION);
			
			ResumenDiarioReversionType resumenDiarioReversionesType = of.createResumenDiarioReversionType();
			ComprobanteReversionType reversionComprobanteType;
			RetencionBEAN encabezado;
			
			List<IdProceso> toUpdate = new ArrayList<IdProceso>();
			for (IdProceso id : comprobantes) {
				serie = dao.getSerie(id);
				encabezado = dao.getEncabezadoRetencion(id);
				
				if (encabezado.getEstado().intValue() == Constantes.MICHELL_ESTADO_RETENCION_REVERSION.ACTIVO.getCode()) {
					logger.info("Retencion activa");
					continue;
				}
				
				reversionComprobanteType = of.createComprobanteReversionType();
				reversionComprobanteType.setSerie(serie.getSerie());
				reversionComprobanteType.setCorrelativo(BigInteger.valueOf(id.getCorrelativoComprobante()));
				
				if (encabezado.getSustento() != null)
					reversionComprobanteType.setMotivo(encabezado.getSustento());
				
				else {
					logger.warn("Motivo o Sustento de Baja de Comprobante [" + serie.getSerie()
					+ "-" + id.getCorrelativoComprobante().toString()
					+ "] no informado.");
					
					continue;
				}
				
				resumenDiarioReversionesType.getComprobante().add(reversionComprobanteType);
				
				if (encabezado.getEstado().intValue() == Constantes.MICHELL_ESTADO_RETENCION_REVERSION.POREMITIR.getCode())
					toUpdate.add(id);
			}
			
			if (toUpdate.size() == 0)
				throw new Exception("Error lógico");

			JAXB.marshal(resumenDiarioReversionesType, System.out);
			
			if (resumenDiarioReversionesType.getComprobante().size() == 0)
				return;
			
			resumenDiarioReversionesType.setEmisor(emisorType);
			resumenDiarioReversionesType.setIdReversion(reversionIDType);

			ComprobanteVinculado comprobante = of.createComprobanteVinculado();
			comprobante.setReversion(resumenDiarioReversionesType);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JAXB.marshal(comprobante, baos );
			baos.flush();
			baos.close();
			
			logger.info(baos.toString());

			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			EmisionService port = service.getEmisionServicePort();
			
			Declarar parameters = new Declarar();
			parameters.setRucEmisor(organizacion.getRuc());
			parameters.setDocumento(baos.toString());
			parameters.setTipoDocumento("0");
			parameters.setFormato(FormatoDeclararType.XM_LV_1_1);
			
			DeclararResponse response = port.declarar(parameters );

			JAXB.marshal(response, System.out);
			logger.info(toUpdate.size());
			
			if (response.getReturn().getRespuesta().getEstado() == 2) {
				dao.updateRetencionesReversadas(toUpdate, Constantes.MICHELL_ESTADO_RETENCION_REVERSION.EMITIDO.getCode());

				marcaPDF(toUpdate);
			} else
				throw new Exception(response.getReturn().getRespuesta().getDescripcion());
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void marcaPDF(List<IdProceso> comprobantes) throws Exception {
		String mark = "REVERSADO [" + FormatoFecha.convertFecha(new Date()) + "]";
		for (IdProceso id : comprobantes) {
			byte[] pdf = dao.getPDFRetenciones(id);
			id.setPdf(WaterMark.newInstancia().mark(pdf, mark));
			dao.changePDFRetenciones(id);
		}
		
	}
}
