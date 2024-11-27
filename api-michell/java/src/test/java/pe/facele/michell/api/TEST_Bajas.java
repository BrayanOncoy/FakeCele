package pe.facele.michell.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TEST_Bajas {
	String salida = new String();
	Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
	
	public static void main(String[] args) throws IOException {
		TEST_Bajas it = new TEST_Bajas();
//		it.doit();
		it.doit2();
//		it.doit3();
	}
	
	private void doit3() throws IOException {
		Path path = Paths.get(System.getProperty("user.home"), "Downloads", "bajas", "MICHELL_DB.csv");

		List<String> list_1 = Files.readAllLines(path, StandardCharsets.UTF_8);
		List<String> list_2 = new ArrayList<String>();
		for (Entry<String, Map<String, String>> next : map.entrySet()) {
//			sac:DocumentSerialID
//			sac:DocumentNumberID
			list_2.clear();
			list_2.addAll(list_1);
			list_1.clear();
			for (String line : list_2) {
				String[] strs = line.split(";");
				System.out.println("strs[0][" + strs[0]
						+ "], next.getValue().get(sac:DocumentSerialID)[" + next.getValue().get("sac:DocumentSerialID")
						+ "], strs[1][" + strs[1] + ",00"
						+ "], next.getValue().get(sac:DocumentNumberID)[" + next.getValue().get("sac:DocumentNumberID")
						+ "]");
				if (strs[0].equals(next.getValue().get("sac:DocumentSerialID")) 
						&& (strs[1] + ",00").contains(next.getValue().get("sac:DocumentNumberID")))
					line = "-" + line + ";" + next.getKey();
				
				list_1.add(line);
			}
			
		}
		Files.write(path.getParent().resolve(System.currentTimeMillis() + "MDB.csv"), list_1, StandardCharsets.UTF_8);
		
	}

	private void doit2() {
		Path path = Paths.get(System.getProperty("user.home"), "Downloads", "bajas");
		DirectoryStream<Path> directory = null;
		try {
			directory = Files.newDirectoryStream(path);
			for (Path p : directory) {
				if (Files.isDirectory(p))
					continue;
				
				if (!p.toString().endsWith("xml"))
					continue;
				
				System.out.println("\n" + p);
				getData(p);
			}
			
			Files.write(path.resolve(System.currentTimeMillis() + "salida.csv"), salida.getBytes(), StandardOpenOption.CREATE_NEW);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {directory.close();} 
			catch (IOException e) {e.printStackTrace();}
		}
		
	}

	private void getData(Path p) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
        try {
			factory.setNamespaceAware(true);
			factory.setIgnoringElementContentWhitespace(false);
			
			org.w3c.dom.Document XMLDoc = factory.newDocumentBuilder().parse(new ByteArrayInputStream (Files.readAllBytes(p)));
			
			Element raiz = XMLDoc.getDocumentElement();

			String id = raiz.getElementsByTagName("cbc:ID").item(0).getFirstChild().getNodeValue();
			System.out.println(id);
			salida += "\n";
//			salida += "Path: " + p.toString() + "\n"; 
			
			NodeList nodes = raiz.getElementsByTagName("sac:VoidedDocumentsLine");
			
			String strName;
			String strValue;
			for (int i=0; i<nodes.getLength() ; i++) {
				Map<String, String> m = new HashMap<>();
				Node n1 = nodes.item(i).getFirstChild();
				strName = n1.getNextSibling().getNodeName();
				strValue = n1.getNextSibling().getFirstChild().getNodeValue();
				System.out.println(strName + ": " + strValue);
				salida += strValue + "|";
				m.put(strName, strValue);
				
				n1 = n1.getNextSibling().getNextSibling();
				strName = n1.getNextSibling().getNodeName();
				strValue = n1.getNextSibling().getFirstChild().getNodeValue();
				System.out.println(strName + ": " + strValue);
				salida += strValue + "|";
				m.put(strName, strValue);
				
				n1 = n1.getNextSibling().getNextSibling();
				strName = n1.getNextSibling().getNodeName();
				strValue = n1.getNextSibling().getFirstChild().getNodeValue();
				System.out.println(strName + ": " + strValue);
				salida += strValue + "|";
				m.put(strName, strValue);
				
				n1 = n1.getNextSibling().getNextSibling();
				strName = n1.getNextSibling().getNodeName();
				strValue = n1.getNextSibling().getFirstChild().getNodeValue();
				System.out.println(strName + ": " + strValue);
				salida += strValue + "|";
				m.put(strName, strValue);
				
				n1 = n1.getNextSibling().getNextSibling();
				strName = n1.getNextSibling().getNodeName();
				strValue = n1.getNextSibling().getFirstChild().getNodeValue();
				System.out.println(strName + ": " + strValue);
				salida += strValue.replace("\n", "") + "|";
				m.put(strName, strValue);

				salida += id + "|\n";
				map.put(id +"_"+ i, m);
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new Exception("Error transformando XML to org.w3c.dom.Document: " + e.getMessage(), e);
		}
		
	}

	private void doit() throws IOException {
		Path p = Paths.get(System.getProperty("user.home"), "Downloads", "michell_VoidedDocuments.txt");
		
		List<String> array = Files.readAllLines(p, StandardCharsets.UTF_8);
		for (String str : array) {
			if (str.contains("El archivo de comunicacion de baja ya fue presentado anteriormente"))
				continue;
			
			if (!str.startsWith("<?xml"))
				continue;
			
			str = str.substring(str.indexOf("<cbc:ReferenceID>"));
			str = str.substring(0,  str.indexOf("</cbc:Description>") + "</cbc:Description>".length());
			
			System.out.println(str);
		}
		
	}
}
