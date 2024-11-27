package pe.facele.michell.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLRecoverableException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import pe.facele.michell.task.RunnableEmisionRetencion;

public class TESTRunnableEmisionRetencion {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public static void main(String[] args) {
		TESTRunnableEmisionRetencion it = new TESTRunnableEmisionRetencion();
		it.doit();
//		it.doitDAO();

	}

	private void doitDAO() {
		logger.debug("WatchTaskComprobantes...");

		ResultSet rs = null;

		IdProceso bean;
		Connection con;
		Statement stm;
		
		try {
			logger.info(Constantes.ORGANIZACON_IDS.toString());
			con = DataSourceFactory.getConnectionStatic();
			stm = con.createStatement();
			
			logger.debug("SELECT R.NRO_INTERNO, R.NRO_COMPROBANTE FROM Z10.RETENCIONES R, Z10.SERIES S WHERE R.NRO_INTERNO=S.NRO_INTERNO AND S.IDORGANIZACION=100 ORDER BY R.FECHA_EMISION DESC");
			rs = stm.executeQuery("SELECT R.NRO_INTERNO, R.NRO_COMPROBANTE FROM Z10.RETENCIONES R, Z10.SERIES S WHERE R.NRO_INTERNO=S.NRO_INTERNO AND S.IDORGANIZACION=100 ORDER BY R.FECHA_EMISION DESC");

			int count = 0;
			while (rs.next()) {
				logger.info("regisgtro: " + count++);
				
				bean = new IdProceso();
				bean.setOrganizacionID(100);
				bean.setNumeroInterno(rs.getLong(1));
				bean.setCorrelativoComprobante(rs.getLong(2));
				
				procesar(bean);
			}

		} catch (SQLRecoverableException e) {
			logger.error("ERROR: " + e.getMessage(), e);
			try {
				con = DataSourceFactory.getConnectionStatic();
				stm = con.createStatement();
			} catch (Exception e1) {e1.printStackTrace();}
		
		} catch (Exception e) {
			logger.error("EXCEPTION: " + e.getMessage(), e);
		
		} finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {logger.error(e,e);}
		}		
	}

	private void procesar(IdProceso bean) {

		final RunnableEmisionRetencion workerEmisionRetencion = new RunnableEmisionRetencion(bean);
		workerEmisionRetencion.run();
		
	}

	private void doit() {
		IdProceso bean = new IdProceso();
		bean.setNumeroInterno(1385L);
		bean.setCorrelativoComprobante(384L);
		RunnableEmisionRetencion workerEmisionRetencion = new RunnableEmisionRetencion(bean);
		Constantes.EXECUTOR.execute(workerEmisionRetencion);
		
		Constantes.EXECUTOR.shutdown();
		Constantes.SCHEDULE.shutdown();
	}
}
