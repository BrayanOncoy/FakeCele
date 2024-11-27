package pe.facele.michell.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import pe.facele.michell.api.Resource;

public class DataSourceFactory {

	static Logger logger = Logger.getLogger(DataSourceFactory.class);
	private static Connection static_con;


	public static Connection getConnectionStatic() throws Exception {

		logger.info("Solicitando con static_con.");
		
		if (static_con != null) {
			try {
				if (!static_con.isClosed()) {
					PreparedStatement pst = static_con.prepareStatement("SELECT * FROM dual");
					logger.info(pst.toString());
					pst.executeQuery();
					pst.close();
				} else {
					logger.info("Conexion cerrado");
					static_con = null;
				}
			} catch (Exception e) {
				logger.error(e, e);
				try {static_con.close();}
				catch (SQLException e1) {logger.fatal("No se puede probar conexion con la base de datos");}
				static_con = null;
			}
		}

		if (static_con == null) {
			logger.info("Re-Obteniendo  static_con.");
			static_con = getConnection();
		}
		
		return static_con;
	}
	
	public static Connection getConnection() throws Exception {
		logger.info("Obteniendo  con dinamica.");
		try {
			Class.forName(Resource.getString("BD.DRIVER"));

			logger.debug(Constantes.DATA.get("urldb", null) + " "
					+ Resource.getString("BD.USUARIO"));
			
			Connection con = DriverManager.getConnection(Constantes.DATA.get("urldb", null), Resource
					.getString("BD.USUARIO"), Resource
					.getString("BD.PASSWORD"));
			
			
			con.setAutoCommit(false);

			logger.info("Obtenida  con dinamica.");
			return con;
			
		} catch (SQLException sqle) {
			logger.error("DataSourceFactory.getConnectionDirectly() failed! "
							+ Constantes.urldb
							+ Resource.getString("BD.USUARIO")
							+ Resource.getString("BD.PASSWORD"));
			
			throw new Exception("DataSourceFactory.getConnectionDirectly() failed! " + sqle.getMessage(), sqle);
		}
	}


	public static void desconectar(Connection con) {
		if (con != null)
			try {
				con.close();
				logger.debug("Desconectado desde BD");
			} catch (SQLException sqle) {
				logger.fatal("DataSourceFactory.desconectar() failed!" + sqle, sqle);
			}
	}
}