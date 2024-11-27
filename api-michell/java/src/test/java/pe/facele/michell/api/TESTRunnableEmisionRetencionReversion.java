package pe.facele.michell.api;

import pe.facele.michell.task.RunnableEmisionRetencionReversion;

public class TESTRunnableEmisionRetencionReversion {

	public static void main(String[] args) {

		RunnableEmisionRetencionReversion workerEmisionRetencionReversion = new RunnableEmisionRetencionReversion(100);
		Constantes.EXECUTOR.execute(workerEmisionRetencionReversion);
		

		Constantes.EXECUTOR.shutdown();
		Constantes.SCHEDULE.shutdown();
	}
}
