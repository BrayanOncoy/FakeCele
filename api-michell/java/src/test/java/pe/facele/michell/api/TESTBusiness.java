package pe.facele.michell.api;

import java.sql.Connection;

import org.apache.log4j.Logger;
public class TESTBusiness {
	Logger logger = Logger.getLogger(this.getClass());
	DAO dao;

	public void start() {
		logger.debug("Start...");
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);
			
//			dao.getDocumentos();

			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			DataSourceFactory.desconectar(con);
		}
		
	}

}
