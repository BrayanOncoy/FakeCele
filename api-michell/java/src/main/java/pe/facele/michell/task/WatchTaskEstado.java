package pe.facele.michell.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pe.facele.docele.dol.services.emisionservice.Consultar;
import pe.facele.docele.dol.services.emisionservice.ConsultarResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoObtenerType;
import pe.facele.docele.dol.services.emisionservice.Obtener;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.FormatoFecha;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.OraclePDF;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.SerieBEAN;

public class WatchTaskEstado extends TimerTask {
	private Logger logger = Logger.getLogger(this.getClass());
	private DAO dao;
	private Calendar cal;
	
	@Override
	public void run() {
		logger.info("Inicia proceso actualizacion de WatchTaskEstado");
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);
			
			cal =  Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			
			for (Integer organizacionID : Constantes.ORGANIZACON_IDS) {
				List<IdProceso> ids = dao.getComprobantesEmitidos(organizacionID);
				logger.info("Cantidad de Comprobantes pendientes de actualizar estado: " + ids.size());

				for (IdProceso id : ids) {
					actualizaEstado(id);
					con.commit();
				}
			}
			

		
		} catch (Exception e) {
			if (e != null && e.getMessage() != null && e.getMessage().contains("ERR_COM"))
				logger.warn(e.getMessage());
			else
				logger.error(e, e);
			try {con.rollback();} catch (SQLException e1) {e1.printStackTrace();}
		} finally {
			DataSourceFactory.desconectar(con);
		}
		
	}

	private void actualizaEstado(IdProceso bean) throws Exception {
		logger.debug("Actualizando estado CorrelativoInterno[" + bean.getNumeroInterno().toString()
				+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
				+ "] y fechaEmision[" + bean.getFechaEmision().toString()
				+ "]");
		try {
			SerieBEAN serie = dao.getSerie(bean);
			
			if (Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).equals(Constantes.MICHELL_TIPO_DOCUMENTO.BOLETA) && bean.getFechaEmision().after(cal.getTime())) {
				logger.info("NO se actualiza estado CorrelativoInterno[" + bean.getNumeroInterno().toString()
						+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
						+ "] y fechaEmision[" + bean.getFechaEmision().toString()
						+ "] por ser una BOLETA de hoy dia.");
				return;
			}
			
			if (serie.getIdOrganizacion().compareTo(bean.getOrganizacionID()) != 0) {
				
				logger.warn("NO se actualiza estado CorrelativoInterno[" + bean.getNumeroInterno().toString()
						+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
						+ "] y fechaEmision[" + bean.getFechaEmision().toString()
						+ "] por ser una OrganizacionID diferente.");
				
				return;
				
			}
			OrganizacionBEAN organizacion = dao.getOrganizacion(serie.getIdOrganizacion());
			

			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			EmisionService port = service.getEmisionServicePort();
			
			Consultar parameters = new Consultar();
			parameters.setRucEmisor(organizacion.getRuc());
			parameters.setTipoDocumento(Integer.toString(Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).name()).getCode()));
			parameters.setSerie(serie.getSerie());
			parameters.setCorrelativo(bean.getCorrelativoComprobante().toString());
			
			Obtener parametersObtener = new Obtener();
            parametersObtener.setRucEmisor(organizacion.getRuc());
            parametersObtener.setTipoDocumento(Integer.toString(Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).name()).getCode()));
            parametersObtener.setSerie(serie.getSerie());
            parametersObtener.setCorrelativo(bean.getCorrelativoComprobante().toString());
            parametersObtener.setCantidad(1);
            parametersObtener.setFormato(FormatoObtenerType.PDF);
			
			ConsultarResponse response = port.consultar(parameters);
			logger.debug(response.getReturn().getRespuesta().getDescripcion());
			
			if (response.getReturn().getRespuesta().getEstado() == 2) {

				logger.debug("estadoSUNAT: " + response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT());
				switch (Constantes.FACELE_ESTADO.fromValue(Integer.parseInt(response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT()))) {
				
				case ACEPTADO_SUNAT:					
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.ARPOBADO.getCode(), Constantes.FACELE_ESTADO.ACEPTADO_SUNAT.toString() + ": " + response.getReturn().getRegistros().getRegistro().get(0).getDescripcionSUNAT());												
					logger.info("Actualizando estado[" + Constantes.FACELE_ESTADO.ACEPTADO_SUNAT.name()
						+ "] CorrelativoInterno[" + bean.getNumeroInterno().toString()
						+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
						+ "]");
					break;
				
				case RECHAZO_SUNAT:
					String mark = "RECHAZADO [" + FormatoFecha.convertFecha(new Date()) + "]";

		            OraclePDF.newInstancia().getPDF(bean, dao, mark);
					
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.RECHAZO.getCode(), Constantes.FACELE_ESTADO.RECHAZO_SUNAT.toString() + ": " + response.getReturn().getRegistros().getRegistro().get(0).getDescripcionSUNAT());
					logger.info("Actualizando estado[" + Constantes.FACELE_ESTADO.RECHAZO_SUNAT.name()
						+ "] CorrelativoInterno[" + bean.getNumeroInterno().toString()
						+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
						+ "]");
					break;
				
				case ACEPTADA_CON_REPAROS_SUNAT:
					dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.CERRADO.getCode(), Constantes.MICHELL_ESTADO_COMPROBANTE.REPAROS.getCode(), Constantes.FACELE_ESTADO.ACEPTADA_CON_REPAROS_SUNAT.toString() + ": " + response.getReturn().getRegistros().getRegistro().get(0).getDescripcionSUNAT());
					logger.info("Actualizando estado[" + Constantes.FACELE_ESTADO.ACEPTADA_CON_REPAROS_SUNAT.name()
						+ "] CorrelativoInterno[" + bean.getNumeroInterno().toString()
						+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
						+ "]");
					break;
					
				case EN_DOCELE:
					logger.info("Documento emitido pero no se tiene resuesta de SUNAT");
					break;
				
				default:
					logger.warn("estado Comprobante en Docele: " + response.getReturn().getRegistros().getRegistro().get(0).getEstadoSUNAT());
					break;
				}
				
			} else
				throw new Exception(response.getReturn().getRespuesta().getDescripcion());
			
		} catch (Exception e) {
			if (e != null && e.getMessage() != null && e.getMessage().contains("No se pudo contactar con docele"))
				throw new Exception("ERR_COM: " + e.getMessage(), e);
				
			if (e.getMessage() != null && e.getMessage() != null && e.getMessage().contains("Comprobante no encontrado con los filtros seleccionados"))
				logger.warn(e.getMessage());
			else
				logger.error(e, e);
		}

	}

}
