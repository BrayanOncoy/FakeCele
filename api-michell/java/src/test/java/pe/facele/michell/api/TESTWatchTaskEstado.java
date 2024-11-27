package pe.facele.michell.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;

import javax.xml.bind.JAXB;

import pe.facele.michell.task.WatchTaskEstado;

@SuppressWarnings("unused")
public class TESTWatchTaskEstado {

	public static void main(String[] args) {
		TESTWatchTaskEstado it = new TESTWatchTaskEstado();
		try {
			System.out.println("Start");
			it.doit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void markBAJA(long nroInterno, long numeroComprobante) {}

	private void markRetencion(long nroInterno, long numeroComprobante) {}

	private void doit() throws Exception {
		
		WatchTaskEstado runnner = new WatchTaskEstado();
		runnner.run();

		System.out.println("End");
	}
	

}
