package pe.facele.michell.task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.IdProceso;

public class RunnableSucursal implements Runnable {
	Logger logger = Logger.getLogger(this.getClass());
	private DAO dao;
	Path p = Paths.get(System.getProperty("user.home"), "Facele", "BackUP");

	@Override
	public void run() {
		logger.info("Tareas Sucursales: " + p.toString());
		Connection con = null;
		try {
			if (Files.notExists(p))
				Files.createDirectories(p);
			
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
			cal.set(GregorianCalendar.MINUTE, 0);
			cal.add(GregorianCalendar.HOUR_OF_DAY, -1);
			Date date = cal.getTime();
			List<IdProceso> comprobantes = dao.getComprobantesEmitidos(Constantes.MICHELL_ESTADO_COMPROBANTE.EMITIDO.getCode());
			List<IdProceso> toBackup = new ArrayList<IdProceso>();
			for (IdProceso id : comprobantes) {
				if (id.getFechaEmision().after(date))
					continue;
				
				toBackup.add(id);
			}
			if (toBackup.size() == 0 )
				return;
			
			backUp(comprobantes);
			
		} catch (Exception e) {
			logger.error(e, e);
			try {con.rollback();} catch (SQLException e1) {e1.printStackTrace();}
		} finally {
			DataSourceFactory.desconectar(con);
		}

	}

	private void backUp(List<IdProceso> comprobantes) {
		Path path;
		for (IdProceso id : comprobantes) {
			
			try {
				path = p.resolve("Interno" + id.getNumeroInterno().toString()
						+ "Comprobante" + id.getCorrelativoComprobante().toString()
						+ ".xml");
				
				if (Files.exists(path))
					continue;
				
				byte[] xml = dao.getComprobante(id);
				Files.write(path, xml, StandardOpenOption.CREATE_NEW);
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
