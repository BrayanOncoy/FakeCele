package pe.facele.michell.task;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import pe.facele.michell.api.Constantes;

public class WatchTaskReversion extends TimerTask {
	Logger logger = Logger.getLogger(this.getClass());

	public WatchTaskReversion() throws Exception {
	}

	@Override
	public void run() {
		logger.info("WatchTaskReversion...");
		for (Integer organizacionID : Constantes.ORGANIZACON_IDS) {
			RunnableEmisionRetencionReversion workerEmisionRetencionReversion = new RunnableEmisionRetencionReversion(organizacionID);
			Constantes.EXECUTOR.execute(workerEmisionRetencionReversion);
		}
	}
}
