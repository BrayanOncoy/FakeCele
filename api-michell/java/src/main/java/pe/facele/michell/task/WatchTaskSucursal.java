package pe.facele.michell.task;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import pe.facele.michell.api.Constantes;

public class WatchTaskSucursal extends TimerTask {
	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void run() {
		logger.info("WatchTaskSucursal...");
		
		RunnableSucursal workerSucursal = new RunnableSucursal();
		Constantes.EXECUTOR.execute(workerSucursal);
		
	}

}
