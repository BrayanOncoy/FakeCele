package pe.facele.michell.api;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.xml.bind.JAXB;
import pe.facele.docele.dol.services.emisionservice.Consultar;
import pe.facele.docele.dol.services.emisionservice.ConsultarResponse;
import pe.facele.docele.dol.services.emisionservice.Declarar;
import pe.facele.docele.dol.services.emisionservice.DeclararResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoDeclararType;
import pe.facele.docele.dol.services.emisionservice.FormatoObtenerType;
import pe.facele.docele.dol.services.emisionservice.Obtener;
import pe.facele.docele.dol.services.emisionservice.ObtenerResponse;

@SuppressWarnings("unused")
public class TESTWS {

	public static void main(String[] args) {
		TESTWS it = new TESTWS();
//		it.consultar();
		it.obtieneDocuemento();
//		it.declaraDocuemento();
//		it.declaraDocuementoUYSA();
	}

	private void declaraDocuementoUYSA() {
		try {
			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			
			EmisionService port = service.getEmisionServicePort();
			
			Declarar parameters = new Declarar();
			parameters.setRucEmisor("20131300353");
			parameters.setTipoDocumento("01");
			parameters.setFormato(FormatoDeclararType.XM_LV_1_1);
			Path path = Paths.get(System.getProperty("user.home"), "Downloads", "expor3.xml");
			parameters.setDocumento(new String(Files.readAllBytes(path )));
			
			DeclararResponse response = port.declarar(parameters );
			JAXB.marshal(response, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void consultar() {
		try {
			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			
			EmisionService port = service.getEmisionServicePort();

			Consultar parameters =  new Consultar();
			parameters.setRucEmisor("20100192650");
			parameters.setTipoDocumento("20");
			parameters.setSerie("R001");
//			parameters.setCorrelativo("175");
			
			System.out.println(service.getWSDLDocumentLocation().toString());
			JAXB.marshal(parameters, System.out);
			ConsultarResponse response = port.consultar(parameters);
			JAXB.marshal(response, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void declaraDocuemento() {
		try {
			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			
			EmisionService port = service.getEmisionServicePort();

			System.out.println(service.getWSDLDocumentLocation().toString());
			Declarar parameters = new Declarar();
			parameters.setRucEmisor("20100192650");
			parameters.setTipoDocumento("01");
			parameters.setFormato(FormatoDeclararType.XM_LV_1_1);
			Path path = Paths.get(System.getProperty("user.home"), "Downloads", "expor3.xml");
			parameters.setDocumento(new String(Files.readAllBytes(path )));
			
			DeclararResponse response = port.declarar(parameters );
			JAXB.marshal(response, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void obtieneDocuemento() {
		try {
			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			
			EmisionService port = service.getEmisionServicePort();
			
			Obtener parameters = new Obtener();
			parameters.setRucEmisor("20100192650");
			parameters.setTipoDocumento("01");
			parameters.setCantidad(1);
			parameters.setFormato(FormatoObtenerType.PDF);
			parameters.setCorrelativo("152");
			parameters.setSerie("F206");

			System.out.println(service.getWSDLDocumentLocation().toString());

			JAXB.marshal(parameters, System.out);
			ObtenerResponse response = port.obtener(parameters );
			if (parameters.getFormato().equals(FormatoObtenerType.PDF))
				System.out.println(Files.write(Paths.get(System.getProperty("user.home"), "Downloads", parameters.getCorrelativo() + "_" + System.currentTimeMillis()
				+ ".pdf"), response.getReturn().getPDF(), StandardOpenOption.CREATE_NEW));
			else
				JAXB.marshal(response, System.out);

			System.out.println(Files.write(Paths.get(System.getProperty("user.home"), "Downloads", parameters.getCorrelativo() + "_" + System.currentTimeMillis()
			+ ".pdf"), response.getReturn().getXML().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
