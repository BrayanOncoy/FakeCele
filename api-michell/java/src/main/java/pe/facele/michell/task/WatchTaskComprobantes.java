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

public class WatchTaskComprobantes extends TimerTask {
	Logger logger = Logger.getLogger(this.getClass());
	private IdProceso bean;
	private Connection con;
	private static Statement stm;

	public WatchTaskComprobantes(Connection con) throws Exception {
		this.con = con;
		WatchTaskComprobantes.stm = this.con.createStatement();
	}

	@Override
	public void run() {
		logger.debug("WatchTaskComprobantes...");

		ResultSet rs = null;
		Runnable workerEmision;
		
		try {
			for (Integer organizacionID : Constantes.ORGANIZACON_IDS) {
				logger.debug("Buscando nuevos comprobantes para: " + Resource.getNOMBRE(organizacionID));

				String sql = Resource.getSQLResource().getString("COMPROBANTE.ENCABEZADO.CORRELATIVO");
				sql = sql.replace("#ORGANIZACION_ID", organizacionID.toString());
				logger.debug(sql);
				rs = WatchTaskComprobantes.stm.executeQuery(sql);

				while (rs.next()) {
					bean = new IdProceso();
					bean.setOrganizacionID(organizacionID);
					bean.setNumeroInterno(rs.getLong(1));
					bean.setCorrelativoComprobante(rs.getLong(2));
					
					if (!Constantes.TURNO.containsKey(organizacionID + bean.getNumeroInterno().toString() + bean.getCorrelativoComprobante().toString())) {
						Constantes.TURNO.put(organizacionID + bean.getNumeroInterno().toString() + bean.getCorrelativoComprobante().toString(), "Comprobantes");

						logger.info("--->Comprobante a Encolar tiene corralativoInterno[" + bean.getNumeroInterno()
								+ "] y numeroFactura[" + bean.getCorrelativoComprobante()
								+ "]");
						
						workerEmision = new RunnableEmisionComprobante(bean);
						Constantes.EXECUTOR.execute(workerEmision);
					}
				}
			}


			
		} catch (SQLRecoverableException e) {
			logger.error("ERROR: " + e.getMessage(), e);
			try {
				this.con = DataSourceFactory.getConnectionStatic();
				WatchTaskComprobantes.stm = this.con.createStatement();
			} catch (Exception e1) {e1.printStackTrace();}
		} catch (Exception e) {
			logger.error("EXCEPTION: " + e.getMessage(), e);
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {logger.error(e,e);}
		}
	}
}
