package pe.facele.utiles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;


public class JAXBUtil {

	private static Logger logger = Logger.getLogger(JAXBUtil.class);

	private static JAXBUtil instance = new JAXBUtil();

	public static JAXBUtil getInstance() {
		return instance;
	}

	@SuppressWarnings("rawtypes")
	public Object unmarshal(Class klass, Reader reader) throws Exception {

		try {
			// Create a JAXBContext capable of handling classes generated into
			// the org.example package
			JAXBContext jc = JAXBContext.newInstance(new Class[] { klass });

			// Create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();

			// Unmarshal a po instance document into a tree of Java content
			// objects composed of classes from the primer.po package.
			Object o = u.unmarshal(reader);

			// Get XML content.
			// Normalize that unmarshal returns either JAXBElement OR
			// class annotated with @XmlRootElement.
			return (o instanceof JAXBElement ? ((JAXBElement) o).getValue() : o);

		} catch (JAXBException je) {
			throw new Exception("XML incorrecto: " + je.getMessage(), je);
		}
	}

	@SuppressWarnings("rawtypes")
	public byte[] marshal(Class klass, Object o) throws IOException, Exception {

		// Luego se Parsea el XML.
		try {
			JAXBContext jc = JAXBContext.newInstance(new Class[] { klass });

			// Create an Unmarshaller
			Marshaller ma = jc.createMarshaller();
			ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ma.marshal(o, baos);

			return baos.toByteArray();

		} catch (JAXBException je) {
			logger.error(je, je);
			throw new Exception(je.getMessage());
		}
	}
}
