package pe.facele.michell.api;

import pe.facele.michell.task.RunnableEmisionRetencionEstado;

public class TESTRunnableEmisionRetencionEstado {

	public static void main(String[] args) {
		TESTRunnableEmisionRetencionEstado it = new TESTRunnableEmisionRetencionEstado();
		
		it.doit();

	}

	private void doit() {

		IdProceso bean = new IdProceso();
		bean.setNumeroInterno(1385L);
		bean.setCorrelativoComprobante(2322L);
		
		RunnableEmisionRetencionEstado workerEmisionRetencionEstado = new RunnableEmisionRetencionEstado(bean);
		Constantes.EXECUTOR.execute(workerEmisionRetencionEstado);
		
		Constantes.EXECUTOR.shutdown();
	}

}
