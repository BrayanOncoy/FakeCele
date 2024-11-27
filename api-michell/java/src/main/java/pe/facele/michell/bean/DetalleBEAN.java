package pe.facele.michell.bean;

import java.math.BigDecimal;
import java.util.Date;

public class DetalleBEAN {
	String unidadMedida;
	BigDecimal subCantidad;
	BigDecimal cantidad;
	String codigo;
	String descripcion;
	BigDecimal valorUnitario;
	BigDecimal precioUnitario;
	BigDecimal igvMonto;
	Integer igvCodigo;
	BigDecimal descuento;
	BigDecimal valorItem;
	String glosaPago;
	BigDecimal flete;
	BigDecimal seguro;
	String aduanaBultos;
	String tipoIdentificacionAdquiriente;
	String numeroIdentificacionAdquiriente;
	String nombreAdquiriente;
	String direccionAdquiriente;
	String cuentaCorrienteSPOT;
	BigDecimal gastosFinancieros;
	BigDecimal descuentoGlobal;	
	String formaPago;
	BigDecimal montoNetoPago;
	String numeroCuota;
	BigDecimal montoCuotaPago;
	Date fechaVencimientoCuota;
	
	
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public BigDecimal getMontoNetoPago() {
		return montoNetoPago;
	}
	public void setMontoNetoPago(BigDecimal montoNetoPago) {
		this.montoNetoPago = montoNetoPago;
	}
	public String getNumeroCuota() {
		return numeroCuota;
	}
	public void setNumeroCuota(String numeroCuota) {
		this.numeroCuota = numeroCuota;
	}
	public BigDecimal getMontoCuotaPago() {
		return montoCuotaPago;
	}
	public void setMontoCuotaPago(BigDecimal montoCuotaPago) {
		this.montoCuotaPago = montoCuotaPago;
	}
	public Date getFechaVencimientoCuota() {
		return fechaVencimientoCuota;
	}
	public void setFechaVencimientoCuota(Date fechaVencimientoCuota) {
		this.fechaVencimientoCuota = fechaVencimientoCuota;
	}
	public String getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	public BigDecimal getCantidad() {
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public BigDecimal getDescuento() {
		return descuento;
	}
	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}
	public BigDecimal getValorItem() {
		return valorItem;
	}
	public void setValorItem(BigDecimal valorItem) {
		this.valorItem = valorItem;
	}
	public BigDecimal getIgvMonto() {
		return igvMonto;
	}
	public void setIgvMonto(BigDecimal igvMonto) {
		this.igvMonto = igvMonto;
	}
	public Integer getIgvCodigo() {
		return igvCodigo;
	}
	public void setIgvCodigo(Integer igvCodigo) {
		this.igvCodigo = igvCodigo;
	}
	public BigDecimal getSubCantidad() {
		return subCantidad;
	}
	public void setSubCantidad(BigDecimal subCantidad) {
		this.subCantidad = subCantidad;
	}
	public String getGlosaPago() {
		return glosaPago;
	}
	public void setGlosaPago(String glosaPago) {
		this.glosaPago = glosaPago;
	}
	public BigDecimal getFlete() {
		return flete;
	}
	public void setFlete(BigDecimal flete) {
		this.flete = flete;
	}
	public BigDecimal getSeguro() {
		return seguro;
	}
	public void setSeguro(BigDecimal seguro) {
		this.seguro = seguro;
	}
	public String getAduanaBultos() {
		return aduanaBultos;
	}
	public void setAduanaBultos(String aduanaBultos) {
		this.aduanaBultos = aduanaBultos;
	}
	public String getDireccionAdquiriente() {
		return direccionAdquiriente;
	}
	public void setDireccionAdquiriente(String direccionAdquiriente) {
		this.direccionAdquiriente = direccionAdquiriente;
	}
	public String getCuentaCorrienteSPOT() {
		return cuentaCorrienteSPOT;
	}
	public void setCuentaCorrienteSPOT(String cuentaCorrienteSPOT) {
		this.cuentaCorrienteSPOT = cuentaCorrienteSPOT;
	}
	public BigDecimal getGastosFinancieros() {
		return gastosFinancieros;
	}
	public void setGastosFinancieros(BigDecimal gastosFinancieros) {
		this.gastosFinancieros = gastosFinancieros;
	}
	public BigDecimal getDescuentoGlobal() {
		return descuentoGlobal;
	}
	public void setDescuentoGlobal(BigDecimal descuentoGlobal) {
		this.descuentoGlobal = descuentoGlobal;
	}
	public String getTipoIdentificacionAdquiriente() {
		return tipoIdentificacionAdquiriente;
	}
	public void setTipoIdentificacionAdquiriente(String tipoIdentificacionAdquiriente) {
		this.tipoIdentificacionAdquiriente = tipoIdentificacionAdquiriente;
	}
	public String getNumeroIdentificacionAdquiriente() {
		return numeroIdentificacionAdquiriente;
	}
	public void setNumeroIdentificacionAdquiriente(String numeroIdentificacionAdquiriente) {
		this.numeroIdentificacionAdquiriente = numeroIdentificacionAdquiriente;
	}
	public String getNombreAdquiriente() {
		return nombreAdquiriente;
	}
	public void setNombreAdquiriente(String nombreAdquiriente) {
		this.nombreAdquiriente = nombreAdquiriente;
	}
}
