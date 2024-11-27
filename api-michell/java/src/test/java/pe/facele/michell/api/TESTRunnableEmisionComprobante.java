package pe.facele.michell.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import pe.facele.michell.task.RunnableEmisionComprobante;

public class TESTRunnableEmisionComprobante {
	private static final int REGISTRO_NUMBER = 26089;

	private Set<Long> setNumero = new HashSet<Long>();
	
	Logger logger = Logger.getLogger(this.getClass());

	public static void main(String[] args) {
		TESTRunnableEmisionComprobante it = new TESTRunnableEmisionComprobante();
//		it.doit();
		it.doitOnlyOne(1938L,1L, 100);
	}

	private void doitOnlyOne(long numeroInterno, long correlativoComprobante, Integer idOrganizacion) {
		IdProceso bean = new IdProceso();
		bean.setNumeroInterno(numeroInterno);
		bean.setCorrelativoComprobante(correlativoComprobante);
		bean.setOrganizacionID(idOrganizacion);
		

		procesar(bean);
		
	}

	private void doit() {
		setNumero.add(1425L);
		setNumero.add(1391L);
		setNumero.add(952L);
		setNumero.add(1433L);
		setNumero.add(1494L);
		setNumero.add(1482L);
		setNumero.add(1454L);
		setNumero.add(1447L);
		setNumero.add(1435L);
		setNumero.add(1961L);
		setNumero.add(1437L);
		setNumero.add(1474L);
		setNumero.add(978L);
		setNumero.add(1453L);
		setNumero.add(1427L);
		setNumero.add(934L);
		setNumero.add(1819L);
		setNumero.add(1449L);
		setNumero.add(1439L);
		setNumero.add(1451L);
		setNumero.add(921L);
		setNumero.add(1404L);
		setNumero.add(1431L);
		setNumero.add(1417L);
		setNumero.add(1480L);
		setNumero.add(1423L);
		setNumero.add(970L);
		setNumero.add(1470L);
		setNumero.add(1421L);
		setNumero.add(1821L);
		setNumero.add(1412L);
		setNumero.add(1485L);
		setNumero.add(1392L);
		setNumero.add(911L);
		setNumero.add(1429L);
		setNumero.add(1402L);
		setNumero.add(1444L);
		setNumero.add(1492L);
		setNumero.add(1419L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);
		setNumero.add(0L);

		logger.debug("WatchTaskComprobantes...");

		ResultSet rs = null;

		IdProceso bean;
		Connection con;
		Statement stm;
		
		try {
			con = DataSourceFactory.getConnectionStatic();
			stm = con.createStatement();
			
			logger.debug("SELECT R.NRO_INTERNO, R.NRO_COMPROBANTE FROM Z10.RETENCIONES R, Z10.SERIES S WHERE R.NRO_INTERNO=S.NRO_INTERNO AND S.IDORGANIZACION=100 ORDER BY R.FECHA_EMISION DESC");
			rs = stm.executeQuery("SELECT R.NRO_INTERNO, R.NRO_COMPROBANTE FROM Z10.RETENCIONES R, Z10.SERIES S WHERE R.NRO_INTERNO=S.NRO_INTERNO AND S.IDORGANIZACION=100 ORDER BY R.FECHA_EMISION DESC");

			int count = 0;
			while (rs.next()) {
				logger.info("regisgtro: " + count++);
				if (count < REGISTRO_NUMBER)
					continue;
				
				bean = new IdProceso();
				bean.setNumeroInterno(rs.getLong(1));
				bean.setCorrelativoComprobante(rs.getLong(2));
				
				if (isOmmited(bean.getNumeroInterno())) {
					logger.info("Es una boleta: " + bean.getNumeroInterno());
					continue;
				}
				
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
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {logger.error(e,e);}
		}
	
	}

	private void procesar(IdProceso bean) {

		RunnableEmisionComprobante workerEmision = new RunnableEmisionComprobante(bean);
		
		workerEmision.run();
		
	}

	private boolean isOmmited(Long numeroInterno) {
		return setNumero.contains(numeroInterno);
	}


}
