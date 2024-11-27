package pe.facele.michell.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import com.itextpdf.text.pdf.BarcodePDF417;

public class TESTImagenPDF417 {

	public static void main(String[] args) throws Exception {
		BarcodePDF417 PDF417 = new BarcodePDF417();
		PDF417.setCodeColumns(10);
		PDF417.setCodeRows(15);
		PDF417.setLenCodewords(999);
		PDF417.setErrorLevel(5);
		PDF417.setOptions(BarcodePDF417.PDF417_FORCE_BINARY);
		PDF417.setText("<TED version=\"1.0\"><DD><RE>78003230-8</RE><TD>39</TD><F>203</F><FE>2016-03-16</FE><RR>66666666-6</RR><RSR>VTA BOLETA COLINA</RSR><MNT>1</MNT><IT1>JOCKEY BASEBALL NARANJA V01</IT1><CAF version=\"1.0\"><DA><RE>78003230-8</RE><RS>Test</RS><TD>39</TD><RNG><D>1</D><H>10000000</H></RNG><FA>2013-12-17</FA><RSAPK><M>ypxklz0JhVZTLfR6sKXnbVSvmTY593C85FPzYziPZOaE2XzBOBSe9nYCoyHda+pDryB3vDPlCYehDReDnZpFpQ==</M><E>Aw==</E></RSAPK><IDK>100</IDK></DA><FRMA algoritmo=\"SHA1withRSA\">d9mPodjCqqSJLenEZlcSdBXIIQtZCY7jQ3lndwodXm3OLrNBxmRCR+/SV9qqynKGmIzojYA/G2Je0fk7nRyH+A==</FRMA></CAF><TSTED>2016-03-17T01:16:24</TSTED></DD><FRMT algoritmo=\"SHA1withRSA\">d0bUhJvbyuIRTX6oCY+9scpMEkX7g3FyeOm+zx+6ho4r2ZCISjtTIyj22EIiBLCQ6ijqr+NH0m7iyQgmQqbdVA==</FRMT></TED>".getBytes("ISO-8859-1"));
		
		java.awt.Image awtImage = PDF417.createAwtImage(Color.BLACK, Color.WHITE);
		BufferedImage bImage= new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bImage.createGraphics();
		g.drawImage(awtImage, 0, 0, null);
		g.dispose();

		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		ImageIO.write(bImage, "jpg", baos);
		baos.flush();
		baos.close();
		
		Files.write(Paths.get(System.getProperty("user.home"), "Downloads", "imagen2.gif"), baos.toByteArray(), StandardOpenOption.CREATE_NEW);
	}

}
