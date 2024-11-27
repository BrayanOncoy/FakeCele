package pe.facele.michell.api;

import pe.facele.michell.task.RunnableEmisionComprobanteBaja;

public class TESTRunnableEmisionComprobanteBaja {

	public static void main(String[] args) {

		RunnableEmisionComprobanteBaja workerEmisionRetencionBaja = new RunnableEmisionComprobanteBaja(100);
		Constantes.EXECUTOR.execute(workerEmisionRetencionBaja);


		
		Constantes.EXECUTOR.shutdown();
		Constantes.SCHEDULE.shutdown();
	}

}
