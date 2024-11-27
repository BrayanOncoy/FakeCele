package pe.facele.michell.task;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import pe.facele.michell.api.Constantes;

public class WatchTaskAnulacion extends TimerTask {
	Logger logger = Logger.getLogger(this.getClass());

	public WatchTaskAnulacion() throws Exception {
	}

	@Override
	public void run() {
		logger.info("WatchTaskAnulacion...");
		for (Integer organizacionID : Constantes.ORGANIZACON_IDS) {
			RunnableEmisionComprobanteBaja workerEmisionRetencionBaja = new RunnableEmisionComprobanteBaja(organizacionID);
			Constantes.EXECUTOR.execute(workerEmisionRetencionBaja);
		}
	}
}
