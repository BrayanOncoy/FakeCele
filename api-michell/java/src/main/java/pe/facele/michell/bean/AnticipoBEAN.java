package pe.facele.michell.bean;

import java.math.BigDecimal;

public class AnticipoBEAN {
	String rucEmisior;
	Integer tipoDocumentoMichell;
	String serie;
	Integer correlativo;
	BigDecimal importeDocumento;
	BigDecimal importeAcumulado;
	
	public String getRucEmisior() {
		return rucEmisior;
	}
	public void setRucEmisior(String rucEmisior) {
		this.rucEmisior = rucEmisior;
	}
	public Integer getTipoDocumentoMichell() {
		return tipoDocumentoMichell;
	}
	public void setTipoDocumentoMichell(Integer tipoDocumentoMichell) {
		this.tipoDocumentoMichell = tipoDocumentoMichell;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public Integer getCorrelativo() {
		return correlativo;
	}
	public void setCorrelativo(Integer correlativo) {
		this.correlativo = correlativo;
	}
	public BigDecimal getImporteDocumento() {
		return importeDocumento;
	}
	public void setImporteDocumento(BigDecimal importeDocumento) {
		this.importeDocumento = importeDocumento;
	}
	public BigDecimal getImporteAcumulado() {
		return importeAcumulado;
	}
	public void setImporteAcumulado(BigDecimal importeAcumulado) {
		this.importeAcumulado = importeAcumulado;
	}

}
