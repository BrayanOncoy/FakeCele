package pe.facele.michell.bean;

import java.util.Date;

public class ReferenciaBEAN {
	Date fechaReferencia;
	Integer tipoDocumento;
	String serie;
	Integer correlativo;
	Integer codigoSunat;
	String sustento;
	
	
	public Integer getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
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
	public Integer getCodigoSunat() {
		return codigoSunat;
	}
	public void setCodigoSunat(Integer codigoSunat) {
		this.codigoSunat = codigoSunat;
	}
	public String getSustento() {
		return sustento;
	}
	public void setSustento(String sustento) {
		this.sustento = sustento;
	}
	public Date getFechaReferencia() {
		return fechaReferencia;
	}
	public void setFechaReferencia(Date fechaReferencia) {
		this.fechaReferencia = fechaReferencia;
	}

}
