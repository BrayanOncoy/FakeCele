package pe.facele.michell.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;

import javax.xml.bind.JAXB;

@SuppressWarnings("unused")
public class TESTWaterMark {

	public static void main(String[] args) {
		TESTWaterMark it = new TESTWaterMark();
		try {
			System.out.println("Start");
//			it.doit();
//			it.markRetencion(1385L, 6L);
//			it.markBAJA(1403L, 1L);
//			it.obtenerPDF(1394L, 195L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	private void markBAJA(long nroInterno, long numeroComprobante) {
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			DAO dao = new DAO(con);
			System.out.println("Start");

			IdProceso bean = new IdProceso();
			bean.setCorrelativoComprobante(numeroComprobante);
			bean.setNumeroInterno(nroInterno);
			JAXB.marshal(bean, System.out);

            Path pathPDFOracle = OraclePDF.newInstancia().getPDF(bean, dao, "BAJA (2016-09-19)");
            
//			byte[] pdf = WaterMark.newInstancia().mark(dao.getPDFComprobantes(bean ), "BAJA (2016-09-19)");
//			bean.setPdf(pdf);
//			dao.changePDFComprobantes(bean);
//			con.commit();
//			System.out.println(Files.write(Paths.get(System.getProperty("user.home"), "Downloads").resolve(System.currentTimeMillis() + ".pdf"), pdf, StandardOpenOption.CREATE_NEW));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceFactory.desconectar(con);
		}
	}

	private void markRetencion(long nroInterno, long numeroComprobante) {
		Connection con = null;
		try {
			con = DataSourceFactory.getConnection();
			DAO dao = new DAO(con);
			System.out.println("Start");

			IdProceso bean = new IdProceso();
			bean.setCorrelativoComprobante(numeroComprobante);
			bean.setNumeroInterno(nroInterno);
			JAXB.marshal(bean, System.out);
			byte[] pdf = WaterMark.newInstancia().mark(dao.getPDFRetenciones(bean ), "REVERSADO (2016-09-09)");
			bean.setPdf(pdf);
			dao.changePDFRetenciones(bean);
			con.commit();

			System.out.println(Files.write(Paths.get(System.getProperty("user.home"), "Downloads").resolve(System.currentTimeMillis() + ".pdf"), pdf, StandardOpenOption.CREATE_NEW));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceFactory.desconectar(con);
		}
		
	}

	private void doit() throws Exception {
		Path p = Paths.get(System.getProperty("user.home"), "Downloads", "PPLPERU_Integracion_MIXTO_v.1.2.pdf");
		
		if (Files.notExists(p))
			throw new Exception("No existe archivo: " + p.toString());
		
		byte[] pdf = WaterMark.newInstancia().mark(Files.readAllBytes(p), "REVERSADO (2016-09-09)");
		
		Files.write(p.getParent().resolve(System.currentTimeMillis() + ".pdf"), pdf, StandardOpenOption.CREATE_NEW);
	}
	
//	private void obtenerPDF(long nroInterno, long numeroComprobante) {
//		Connection con = null;
//		try {
//			con = DataSourceFactory.getConnection(Constantes.EMISOR_RUC);
//			DAO dao = new DAO(con);
//			System.out.println("Start");
//
//			IdProceso bean = new IdProceso();
//			bean.setCorrelativoComprobante(numeroComprobante);
//			bean.setNumeroInterno(nroInterno);
//			JAXB.marshal(bean, System.out);
//			byte[] pdf = dao.getPDFComprobantes(bean);
//
//			System.out.println(Files.write(Paths.get(System.getProperty("user.home"), "Downloads").resolve(System.currentTimeMillis() + ".pdf"), pdf, StandardOpenOption.CREATE_NEW));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			DataSourceFactory.desconectar(con);
//		}
//	}

}
