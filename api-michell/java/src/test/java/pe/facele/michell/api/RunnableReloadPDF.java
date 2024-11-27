package pe.facele.michell.api;

import java.sql.Connection;

import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoObtenerType;
import pe.facele.docele.dol.services.emisionservice.Obtener;
import pe.facele.docele.dol.services.emisionservice.ObtenerResponse;
import pe.facele.michell.bean.SerieBEAN;

public class RunnableReloadPDF implements Runnable {

	private IdProceso bean;
	public RunnableReloadPDF(IdProceso bean) {
		this.bean = bean;
	}

	@Override
	public void run() {
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			DAO dao = new DAO(con);
			
			SerieBEAN serie = dao.getSerie(bean);
			
			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			EmisionService port = service.getEmisionServicePort();
			
			Obtener parametersPDF = new Obtener();
			parametersPDF.setRucEmisor("20100192650");
			parametersPDF.setSerie(serie.getSerie());
			parametersPDF.setCorrelativo(bean.getCorrelativoComprobante().toString());
			parametersPDF.setTipoDocumento("01");
			parametersPDF.setCantidad(1);
			parametersPDF.setFormato(FormatoObtenerType.PDF);
			
			ObtenerResponse responsePDF = port.obtener(parametersPDF);
			
			bean.setPdf(responsePDF.getReturn().getPDF());
			
			dao.changePDFComprobantes(bean);
			con.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceFactory.desconectar(con);
		}
	}

}
