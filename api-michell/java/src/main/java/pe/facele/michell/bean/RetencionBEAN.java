package pe.facele.michell.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class RetencionBEAN {
	Long correlativoInterno;
	Long correlativoComprobante;
	Date fechaEmision;
	Integer estado;
	Integer emisionElectronica;
	String sustento;
	BigInteger numeroMovimientoCaja;
	Long idCorrientista;
	String numeroCheque;
	String asientoContable;
	BigDecimal tasaCambio;
	byte[] xml;
	byte[] pdf;
	
	
	public Long getCorrelativoInterno() {
		return correlativoInterno;
	}
	public void setCorrelativoInterno(Long correlativoInterno) {
		this.correlativoInterno = correlativoInterno;
	}
	public Long getCorrelativoComprobante() {
		return correlativoComprobante;
	}
	public void setCorrelativoComprobante(Long correlativoComprobante) {
		this.correlativoComprobante = correlativoComprobante;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	
	public Integer getEmisionElectronica() {
		return emisionElectronica;
	}
	public void setEmisionElectronica(Integer emisionElectronica) {
		this.emisionElectronica = emisionElectronica;
	}
	
	public String getSustento() {
		return sustento;
	}
	public void setSustento(String sustento) {
		this.sustento = sustento;
	}
	public BigInteger getNumeroMovimientoCaja() {
		return numeroMovimientoCaja;
	}
	public void setNumeroMovimientoCaja(BigInteger numeroMovimientoCaja) {
		this.numeroMovimientoCaja = numeroMovimientoCaja;
	}
	public Long getIdCorrientista() {
		return idCorrientista;
	}
	public void setIdCorrientista(Long idCorrientista) {
		this.idCorrientista = idCorrientista;
	}
	
	public String getNumeroCheque() {
		return numeroCheque;
	}
	public void setNumeroCheque(String numeroCheque) {
		this.numeroCheque = numeroCheque;
	}
	public String getAsientoContable() {
		return asientoContable;
	}
	public void setAsientoContable(String asientoContable) {
		this.asientoContable = asientoContable;
	}
	
	public BigDecimal getTasaCambio() {
		return tasaCambio;
	}
	public void setTasaCambio(BigDecimal tasaCambio) {
		this.tasaCambio = tasaCambio;
	}
	public byte[] getXml() {
		return xml;
	}
	public void setXml(byte[] xml) {
		this.xml = xml;
	}
	public byte[] getPdf() {
		return pdf;
	}
	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}
	
}
