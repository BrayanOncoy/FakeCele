package pe.facele.michell.api;

import java.sql.Connection;

@SuppressWarnings("unused")
public class TESTotros {

	public static void main(String[] args) {
		TESTotros it = new TESTotros();
//		it.connection();
		it.preferencias();

	}

	private void preferencias() {

//		produccion
//		Constantes.DATA.put("wsdlLocation", "http://10.11.2.237:8081/doceleol-webservice-1.0/EmisionService?wsdl");
//		Constantes.DATA.put("modalidad", "");
//		Constantes.DATA.put("urldb", "jdbc:oracle:thin:@10.11.2.5:1521:MIP1");
		
//		desarrollo
		Constantes.DATA.put("wsdlLocation", "http://demo.docele.pe/doceleol-1.0/EmisionService?wsdl");
//		Constantes.DATA.put("modalidad", "caza_matriz");
//		Constantes.DATA.put("urldb", "jdbc:oracle:thin:@10.11.2.236:1521:MIP1");

		System.out.println(Constantes.DATA.get("wsdlLocation", null));
		System.out.println(Constantes.DATA.get("modalidad", null));
		System.out.println(Constantes.DATA.get("urldb", null));
	}

	private void connection() {
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceFactory.desconectar(con);
		}
	}

}
