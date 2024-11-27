package pe.facele.michell.task;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import pe.facele.docele.dol.services.emisionservice.Consultar;
import pe.facele.docele.dol.services.emisionservice.ConsultarResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.SerieBEAN;


public class RunnableEmisionComprobanteEstado implements Runnable {
	Logger logger = Logger.getLogger(this.getClass());

	private IdProceso bean;
	
	@Deprecated
	public RunnableEmisionComprobanteEstado(IdProceso bean) {
		this.bean = bean;
	}

	@Override
	public void run() {
		logger.info("Actualizando estado CorrelativoInterno[" + bean.getNumeroInterno().toString()
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
			parameters.setTipoDocumento(Integer.toString(Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).name()).getCode()));
			parameters.setSerie(serie.getSerie());
			parameters.setCorrelativo(bean.getCorrelativoComprobante().toString());
			
			ConsultarResponse response = port.consultar(parameters);
			logger.info(response.getReturn().getRespuesta().getDescripcion());
			
			if (response.getReturn().getRespuesta().getEstado() == 2) {
				
				switch (Constantes.FACELE_ESTADO.fromValue(Integer.parseInt(response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT()))) {
				
				case ACEPTADO_SUNAT:
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.ARPOBADO.getCode(), Constantes.FACELE_ESTADO.ACEPTADO_SUNAT.toString());					
					con.commit();
					break;
				
				case RECHAZO_SUNAT:
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.RECHAZO.getCode(), Constantes.FACELE_ESTADO.RECHAZO_SUNAT.toString());
					con.commit();
					break;
				
				case ACEPTADA_CON_REPAROS_SUNAT:
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.REPAROS.getCode(), Constantes.FACELE_ESTADO.ACEPTADA_CON_REPAROS_SUNAT.toString());
					con.commit();
					break;

				case EN_DOCELE:
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.EMITIDO.getCode(), Constantes.FACELE_ESTADO.EN_DOCELE.toString());
					con.commit();
					break;
				
				default:
					logger.warn("estado Comprobante en Docele: " + response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT());
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
