package pe.facele.michell.api;

import java.io.ByteArrayOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public final class WaterMark {

	public static WaterMark newInstancia() {
		return new WaterMark();
	}

	public byte[] mark(byte[] readAllBytes, String mark) throws Exception {
		
		try {
			PdfReader reader = new PdfReader(readAllBytes);
			Document documento = new Document(reader.getPageSize(1));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(documento,bos);
			
			documento.open();
			PdfContentByte _canvas = writer.getDirectContentUnder();
			PdfImportedPage page;
			
			_canvas.saveState();
			page = writer.getImportedPage(reader, 1);
			_canvas.addTemplate(page, 0, 0);
			_canvas.restoreState();
			
			_canvas.beginText();
 			PdfContentByte canvasWaterMark = writer.getDirectContent();
			canvasWaterMark.saveState();
			Font FONT = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 60, Font.BOLD, BaseColor.RED);
			ColumnText.showTextAligned(writer.getDirectContentUnder(),
			com.itextpdf.text.Element.ALIGN_CENTER, new Phrase(mark, FONT),
			writer.getPageSize().getWidth()/2, writer.getPageSize().getHeight()/2, 50);
			canvasWaterMark.restoreState();

			_canvas.endText();
			
			_canvas.toPdf(writer);
		
			documento.close();
			
			return bos.toByteArray();
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

}
