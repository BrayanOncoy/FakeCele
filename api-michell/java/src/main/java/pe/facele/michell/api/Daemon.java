package pe.facele.michell.api;

import java.sql.Connection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import pe.facele.michell.task.WatchTaskAnulacion;
import pe.facele.michell.task.WatchTaskComprobantes;
import pe.facele.michell.task.WatchTaskComprobantesVinculados;
import pe.facele.michell.task.WatchTaskComprobantesVinculadosEstado;
import pe.facele.michell.task.WatchTaskEstado;
import pe.facele.michell.task.WatchTaskReversion;
import pe.facele.michell.task.WatchTaskSucursal;

public class Daemon {
	private static Daemon demonio = new Daemon();
	private boolean stopped;
	
	public Daemon() {
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String cmd = "";
		if (args.length > 0) {
			cmd = args[0];
		} else
			throw new Exception("Argumento tiene que ser informado y puede ser [start] o [stop].");

		if ("start".equals(cmd))
			demonio .start();
		
		else if ("stop".equals(cmd))
			demonio.stop();
		else
			throw new Exception("Argumento[" + cmd
					+ "] incorrecto, tiene que ser [start] o [stop].");
	}


	private void stop() {
		Logger.getLogger(this.getClass().getCanonicalName()).error("...Deteniendo Servicio");
		
		stopped = true;
		
		Constantes.EXECUTOR.shutdown();
		Constantes.SCHEDULE.shutdown();

		synchronized (this) {
			this.notify();
		}
	}

	private void start() {
		Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
		logger.info("Start Demonio...");
		logger.info("HOME_FACELE: " + System.getProperty("user.home"));
		logger.info("wsdlLocation: " + Constantes.DATA.get("wsdlLocation", null));
		logger.info("urldb: " + Constantes.DATA.get("urldb", null));
		logger.info("modalidad: " + Constantes.DATA.get("modalidad", null));
		
		Connection con = null;
		WatchTaskComprobantes comprobantes = null;
		WatchTaskComprobantesVinculados comprobantesVinculados = null;
		WatchTaskComprobantesVinculadosEstado comprobantesVinculadosEstado = null;
		WatchTaskAnulacion anulacion =  null;
		WatchTaskReversion reversion = null;
		WatchTaskSucursal sucursal = null;
		WatchTaskEstado estados =  null;
		try {
			while (true) {
				try {
					con = DataSourceFactory.getConnectionStatic();
					break;
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				
				Thread.sleep(2000);
			}
			
			comprobantes = new WatchTaskComprobantes(con);
			Constantes.SCHEDULE.scheduleWithFixedDelay(comprobantes, 2, 1, TimeUnit.SECONDS);

			if (Constantes.DATA.get("modalidad", "casa_matriz").equals("casa_matriz")) {
				comprobantesVinculados = new WatchTaskComprobantesVinculados(con);
				Constantes.SCHEDULE.scheduleWithFixedDelay(comprobantesVinculados, 2, 10, TimeUnit.SECONDS);
				
				comprobantesVinculadosEstado = new WatchTaskComprobantesVinculadosEstado(con);
				Constantes.SCHEDULE.scheduleWithFixedDelay(comprobantesVinculadosEstado, 2, 5, TimeUnit.MINUTES);
				
				reversion = new WatchTaskReversion();
				Constantes.SCHEDULE.scheduleWithFixedDelay(reversion, 2, 10, TimeUnit.MINUTES);
			
			} else {
				sucursal = new WatchTaskSucursal();
				Constantes.SCHEDULE.scheduleWithFixedDelay(sucursal, 0, 1, TimeUnit.HOURS);
			}
			anulacion = new WatchTaskAnulacion();
			Constantes.SCHEDULE.scheduleWithFixedDelay(anulacion, 1, 10, TimeUnit.MINUTES);
			
			estados = new WatchTaskEstado();
			Constantes.SCHEDULE.scheduleWithFixedDelay(estados, 2, 10, TimeUnit.MINUTES);

			while (!stopped) {
				try {
					if (Constantes.EXECUTOR instanceof ThreadPoolExecutor) {
						logger.info("EXECUTOR size Active Count [" +  ((ThreadPoolExecutor) Constantes.EXECUTOR).getActiveCount()
								+ "], Pool Size[" + ((ThreadPoolExecutor) Constantes.EXECUTOR).getPoolSize()
								+ "], size Task done[" +  ((ThreadPoolExecutor) Constantes.EXECUTOR).getTaskCount()
								+ "]");
					}
					
					if (Constantes.SCHEDULE instanceof ThreadPoolExecutor) {
						logger.info("SCHEDULE size Active Count[" + ((ThreadPoolExecutor) Constantes.SCHEDULE).getActiveCount()
								+ "], Pool Size[" + ((ThreadPoolExecutor) Constantes.SCHEDULE).getPoolSize()
								+ "], Task done[" + ((ThreadPoolExecutor) Constantes.SCHEDULE).getTaskCount()
								+ "]");
						
					}
					
					logger.debug("Constantes.EXECUTOR: " + Constantes.EXECUTOR.toString());
					
				} catch (Exception e) {
					logger.error(e, e);
				}

				synchronized (this) {
					try {
						this.wait(10_000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e, e);
			
		} finally {
			DataSourceFactory.desconectar(con);
		}
	}
}
