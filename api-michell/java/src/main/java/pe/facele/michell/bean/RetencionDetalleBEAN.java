package pe.facele.michell.bean;

import java.math.BigDecimal;
import java.util.Date;

public class RetencionDetalleBEAN {
	Integer tipoComprobante;
	String serie;
	Long correlativo;
	Date fechaEmision;
	String moneda;
	BigDecimal importeTotal;
	Date fechaPago;
	Integer nuemeroPago;
	BigDecimal importeRetenidoPEN;
	BigDecimal importePagadoBrutoPEN;
	
	public Integer getTipoComprobante() {
		return tipoComprobante;
	}
	public void setTipoComprobante(Integer tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public Long getCorrelativo() {
		return correlativo;
	}
	public void setCorrelativo(Long correlativo) {
		this.correlativo = correlativo;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}
	public Date getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}
	public Integer getNuemeroPago() {
		return nuemeroPago;
	}
	public void setNuemeroPago(Integer nuemeroPago) {
		this.nuemeroPago = nuemeroPago;
	}
	public BigDecimal getImporteRetenidoPEN() {
		return importeRetenidoPEN;
	}
	public void setImporteRetenidoPEN(BigDecimal importeRetenidoPEN) {
		this.importeRetenidoPEN = importeRetenidoPEN;
	}
	public BigDecimal getImportePagadoBrutoPEN() {
		return importePagadoBrutoPEN;
	}
	public void setImportePagadoBrutoPEN(BigDecimal importePagadoBrutoPEN) {
		this.importePagadoBrutoPEN = importePagadoBrutoPEN;
	}
}
