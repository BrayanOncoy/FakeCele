package pe.facele.michell.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.log4j.Logger;

public final class OraclePDF {
	Logger logger = Logger.getLogger(this.getClass());
	
	private final String pathInicial = "/nas198/dir_facturas/";

	public static OraclePDF newInstancia() {
		return new OraclePDF();
		
	}

	public Path getPDF(IdProceso bean, DAO dao, String mark) throws Exception {
//		"bfilename('DIR_FACTURAS','" + bean.rutaPDF + "')"
		String pathPDF = dao.getPahtPDFComprobantes(bean);
		if (pathPDF == null || pathPDF.isEmpty()) {
			logger.info("no existe Path para comprobante");
			return null;
		}
		
		pathPDF = pathPDF.replace("bfilename('DIR_FACTURAS','", "");
		pathPDF = pathPDF.replace("')", "");
		
		final Path path = Paths.get(pathInicial + pathPDF);
		logger.info("Marcando pdf: " + path.toString());
        
		return Files.write(path, WaterMark.newInstancia().mark(Files.readAllBytes(path), mark), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
