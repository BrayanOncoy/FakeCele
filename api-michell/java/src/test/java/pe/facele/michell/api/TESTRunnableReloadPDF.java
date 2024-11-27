package pe.facele.michell.api;

public class TESTRunnableReloadPDF {

	public static void main(String[] args) {

		IdProceso bean = new IdProceso();
		bean.setNumeroInterno(1385L);
		bean.setCorrelativoComprobante(108L);
		RunnableReloadPDF workerEmision = new RunnableReloadPDF(bean);
		Constantes.EXECUTOR.execute(workerEmision);

		Constantes.EXECUTOR.shutdown();
		Constantes.SCHEDULE.shutdown();}

}
