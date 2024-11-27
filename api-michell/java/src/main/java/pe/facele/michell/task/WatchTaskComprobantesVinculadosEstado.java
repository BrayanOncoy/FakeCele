package pe.facele.michell.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.Resource;

public class WatchTaskComprobantesVinculadosEstado extends TimerTask {
	Logger logger = Logger.getLogger(this.getClass());
	private IdProceso bean;
	private Connection con;
	private static Statement stm;

	public WatchTaskComprobantesVinculadosEstado(Connection con) throws Exception {
		this.con = con;
	}

	@Override
	public void run() {
		logger.info("WatchTaskComprobantesVinculados...");

		ResultSet rs = null;
		ResultSet rsEstadoSunat = null;
		ResultSet rsReversiones = null;
		Runnable workerEmisionRetencionEstado;

		try {
			confirarConnection();

			WatchTaskComprobantesVinculadosEstado.stm = this.con.createStatement();

			for (Integer organizacionId : Constantes.ORGANIZACON_IDS) {

				// Actualiza estados
				String sql = Resource.getSQLResource().getString("RETENCIONES.ENCABEZADO.CORRELATIVO").replace(
						"#MICHELL_ESTADO_RETENCION#", "" + Constantes.MICHELL_ESTADO_RETENCION.EMITIDO.getCode());
				sql = sql.replace("#ORGANIZACION_ID", organizacionId.toString());
				logger.debug(sql);
				
				rsEstadoSunat = WatchTaskComprobantesVinculadosEstado.stm.executeQuery(sql);

				while (rsEstadoSunat.next()) {
					bean = new IdProceso();
					bean.setOrganizacionID(organizacionId);
					bean.setNumeroInterno(rsEstadoSunat.getLong(1));
					bean.setCorrelativoComprobante(rsEstadoSunat.getLong(2));

					if (!Constantes.TURNO.containsKey(organizacionId + bean.getNumeroInterno().toString()
							+ bean.getCorrelativoComprobante().toString())) {
						
						Constantes.TURNO.put(organizacionId + bean.getNumeroInterno().toString()
								+ bean.getCorrelativoComprobante().toString(), "RetencionesEstado");
						
						workerEmisionRetencionEstado = new RunnableEmisionRetencionEstado(bean);
						
						Constantes.EXECUTOR.execute(workerEmisionRetencionEstado);
					}
				}
			}

		} catch (SQLRecoverableException e) {
			logger.error(e.getMessage(), e);
			try {
				this.con = DataSourceFactory.getConnectionStatic();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (rsEstadoSunat != null)
				try {
					rsEstadoSunat.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (rsReversiones != null)
				try {
					rsReversiones.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	private void confirarConnection() throws Exception {
		if (con.isValid(3))
			return;

		this.con = DataSourceFactory.getConnectionStatic();
	}

}
