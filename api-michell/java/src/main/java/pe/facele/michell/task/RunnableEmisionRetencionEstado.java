package pe.facele.michell.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import pe.facele.docele.dol.services.emisionservice.Consultar;
import pe.facele.docele.dol.services.emisionservice.ConsultarResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.FormatoFecha;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.WaterMark;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.SerieBEAN;

public class RunnableEmisionRetencionEstado implements Runnable {
	Logger logger = Logger.getLogger(this.getClass());

	private IdProceso bean;

	public RunnableEmisionRetencionEstado(IdProceso bean) {
		this.bean = bean;
	}

	@Override
	public void run() {
		logger.info("Estado retencion CorrelativoInterno[" + bean.getNumeroInterno().toString()
				+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
				+ "]");
		Connection con = null;
		DAO dao;
		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);
			SerieBEAN serie = dao.getSerie(bean);
			
			if (serie.getIdOrganizacion().compareTo(bean.getOrganizacionID()) != 0)
				return;
			
			OrganizacionBEAN organizacion = dao.getOrganizacion(serie.getIdOrganizacion());

			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			EmisionService port = service.getEmisionServicePort();
			
			Consultar parameters = new Consultar();
			parameters.setRucEmisor(organizacion.getRuc());
			parameters.setTipoDocumento("20");
			parameters.setSerie(serie.getSerie());
			parameters.setCorrelativo(bean.getCorrelativoComprobante().toString());
			ConsultarResponse response = port.consultar(parameters );
			
			JAXB.marshal(response, System.out);
			
			if (response.getReturn().getRespuesta().getEstado() == 2) {
				switch (Constantes.FACELE_ESTADO.fromValue(Integer.parseInt(response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT()))) {
				case ACEPTADO_SUNAT:
					dao.updateRetencionEstado(bean, Constantes.MICHELL_ESTADO_RETENCION.APROBADO.getCode());
					con.commit();
					break;
				
				case RECHAZO_SUNAT:
					String mark = "RECHAZADO [" + FormatoFecha.convertFecha(new Date()) + "]";
					byte[] pdf = dao.getPDFRetenciones(bean);
					bean.setPdf(WaterMark.newInstancia().mark(pdf, mark));
					dao.changePDFRetenciones(bean);
					dao.updateRetencionEstado(bean, Constantes.MICHELL_ESTADO_RETENCION.RECHAZADO.getCode());
					con.commit();
					break;
				
				default:
					logger.info("Documento con estado indefinido[" + response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT()
							+ "].");
					break;
				
				}

			} else
				throw new Exception(response.getReturn().getRespuesta().getDescripcion());

		} catch (Exception e) {
			logger.error(e, e);
			try {con.rollback();} catch (SQLException e1) {e1.printStackTrace();}
		} finally {
			DataSourceFactory.desconectar(con);
			Constantes.TURNO.remove(bean.getOrganizacionID() + bean.getNumeroInterno().toString() + bean.getCorrelativoComprobante().toString());
		}

	}

}
