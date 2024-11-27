package pe.facele.michell.api;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import pe.facele.docele.dol.services.emisionservice.Declarar;
import pe.facele.docele.dol.services.emisionservice.DeclararResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoDeclararType;
import pe.facele.docele.schemas.v11.comprobante.BajaComprobanteType;
import pe.facele.docele.schemas.v11.comprobante.BajaIDType;
import pe.facele.docele.schemas.v11.comprobante.BajaType;
import pe.facele.docele.schemas.v11.comprobante.Comprobante;
import pe.facele.docele.schemas.v11.comprobante.DireccionPostalType;
import pe.facele.docele.schemas.v11.comprobante.EmisorType;
import pe.facele.docele.schemas.v11.comprobante.ObjectFactory;
import pe.facele.docele.schemas.v11.comprobante.TipoComprobanteType;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.FormatoFecha;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.OraclePDF;
import pe.facele.michell.bean.EncabezadoBEAN;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.SerieBEAN;

public class RunnableEmisionComprobanteBajaTest implements Runnable {
	Logger logger = Logger.getLogger(this.getClass());
	private DAO dao;
	

	public RunnableEmisionComprobanteBajaTest() {
	}

	@Override
	public void run() {
		logger.info("Generando Bajas de Comprobantes");
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);
			
			List<IdProceso> porAnular = dao.getComprobantesBaja(100, Constantes.MICHELL_ESTADO_COMPROBANTE_BAJA.EMITIDO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.ARPOBADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.REPAROS.getCode());
			logger.debug("Cantidad regisgtros por anular: " + porAnular.size());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -2);
			for (IdProceso id : porAnular) {
				if (id.getFechaEmision().before(cal.getTime()))
					continue;
				
				if (id.getFechaEmision().getMonth() != 1)
					continue;

				
				
				SerieBEAN serie = dao.getSerie(id);
				
				if (serie.getSerie().startsWith("B"))
					continue;
				
				logger.info("comprobante[" + Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).name()
						+ "] tipo[" + serie.getSerie()
						+ "] folio[" + id.getCorrelativoComprobante().toString()
						+ "] fechaEmision[" + id.getFechaEmision().toString()
						+ "]");
			}
			
			
		} catch (Exception e) {
			logger.error(e, e);
			try {con.rollback();} catch (SQLException e1) {e1.printStackTrace();}
		} finally {
			DataSourceFactory.desconectar(con);
		}

	}

	private void marcaPDF(List<IdProceso> comprobantes) throws Exception {
		String mark = "BAJA [" + FormatoFecha.convertFecha(new Date()) + "]";
		for (IdProceso id : comprobantes) {
            OraclePDF.newInstancia().getPDF(id, dao, mark);
            
//			byte[] pdf = dao.getPDFComprobantes(id);
//			if (pdf == null)
//				continue;
//			id.setPdf(WaterMark.newInstancia().mark(pathPDFOracle, mark));
//			dao.changePDFComprobantes(id);
		}
		
	}

	private void generaBaja(List<IdProceso> comprobantes) throws Exception, ParseException {
		try {
			SerieBEAN serie = dao.getSerie(comprobantes.get(0));
			
			if (serie.getIdOrganizacion().compareTo(100) != 0)
				return;
			
			OrganizacionBEAN organizacion = dao.getOrganizacion(serie.getIdOrganizacion());

			ObjectFactory of = new ObjectFactory();
			
			EmisorType emisorType = of.createEmisorType();
			emisorType.setRuc(organizacion.getRuc());
			emisorType.setRazonSocial(organizacion.getDescripcion());
			emisorType.setNombreComercial(organizacion.getDescripcion());
			
			DireccionPostalType domicilioFiscal = of.createDireccionPostalType();
			domicilioFiscal.setUbigeo(Resource.getUBIGEO(serie.getIdOrganizacion())+"");
			domicilioFiscal.setDireccionCompleta(organizacion.getDireccion());
			domicilioFiscal.setUrbanizacion(organizacion.getDistrito());
			domicilioFiscal.setDistrito(organizacion.getDistrito());
			domicilioFiscal.setProvincia(organizacion.getProvincia());
			domicilioFiscal.setDepartamento(organizacion.getDistrito());
			emisorType.setDomicilioFiscal(domicilioFiscal);
			
			BajaIDType bajaIDType = of.createBajaIDType();
			bajaIDType.setFechaEmision(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(new Date())));
			bajaIDType.setFechaEmisionComprobantes(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(comprobantes.get(0).getFechaEmision())));
			
			BajaType bajaType = of.createBajaType();
			BajaComprobanteType bajaComprobanteType;
			EncabezadoBEAN encabezado;
			
			for (IdProceso id : comprobantes) {
				serie = dao.getSerie(id);
				encabezado = dao.getEncabezado(id);
				
				bajaComprobanteType = of.createBajaComprobanteType();
				bajaComprobanteType.setTipoComprobante(TipoComprobanteType.valueOf(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).name()));
				bajaComprobanteType.setSerie(serie.getSerie());
				bajaComprobanteType.setCorrelativo(id.getCorrelativoComprobante().intValue());
				
				if (encabezado.getSustento() != null)
					bajaComprobanteType.setMotivo(encabezado.getSustento());
				
				else {
					bajaComprobanteType.setMotivo("NO INFORMADO");
				}

				bajaType.getComprobante().add(bajaComprobanteType );
			}

			if (bajaType.getComprobante().size() == 0)
				return;
			
			bajaType.setEmisor(emisorType);
			bajaType.setId(bajaIDType);

			Comprobante comprobante = of.createComprobante(); 
			comprobante.setBaja(bajaType );
			
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
			parameters.setTipoDocumento("1000");
			parameters.setFormato(FormatoDeclararType.XM_LV_1_1);
			
			DeclararResponse response = port.declarar(parameters);
			
			JAXB.marshal(response, System.out);
			
			if (response.getReturn().getRespuesta().getEstado() == 2) {
				dao.updateComprobanteBaja(comprobantes, Constantes.MICHELL_ESTADO_COMPROBANTE_BAJA.EMITIDO.getCode());
			} else
				throw new Exception(response.getReturn().getRespuesta().getDescripcion());
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
