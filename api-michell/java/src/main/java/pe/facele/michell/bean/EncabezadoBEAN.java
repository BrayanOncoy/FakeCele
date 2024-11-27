package pe.facele.michell.bean;

import java.math.BigDecimal;
import java.util.Date;

public class EncabezadoBEAN {
	Date fechaEmision;
	Date fechaVencimiento;
	Integer tipoFactura;
	Integer tipoNotaCredito;
	String sustento;
	Integer tipoNotaDebido;
	Date fechaEmbarque;
	Long idCorrientista;
	String ruc;
	String identificacionTipo;
	String identificacionNumero;
	String nombre;
	Integer origen;
	Integer ciudadOrigen;
	Integer destino;
	String moneda;
	String formaPago;
	BigDecimal importeFlete;
	BigDecimal importeSeguro;
	BigDecimal valorFOB;
	BigDecimal porcentageDescuento;
	BigDecimal importeDescuento;
	BigDecimal porcentageIGV;
	BigDecimal tasaIGV;
	BigDecimal importeIGV;
	BigDecimal importeNeto;
	BigDecimal importePrecioBase;
	BigDecimal importeSoles;
	BigDecimal importeDolares;
	BigDecimal saldo;
	BigDecimal saldoAnticipo;
	BigDecimal tipoCambioEmision;
	Integer transportista;
	String transporte;
	Long comisionista;
	String descripcion;
	String observaciones;
	String observaItems;
	String usuarioCreacion;
	Long idVendedor;
	Integer tipoMuestra;
	
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public Date getFechaEmbarque() {
		return fechaEmbarque;
	}
	public void setFechaEmbarque(Date fechaEmbarque) {
		this.fechaEmbarque = fechaEmbarque;
	}
	public Long getIdCorrientista() {
		return idCorrientista;
	}
	public void setIdCorrientista(Long idCorrientista) {
		this.idCorrientista = idCorrientista;
	}
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Integer getOrigen() {
		return origen;
	}
	public void setOrigen(Integer origen) {
		this.origen = origen;
	}
	public Integer getCiudadOrigen() {
		return ciudadOrigen;
	}
	public void setCiudadOrigen(Integer ciudadOrigen) {
		this.ciudadOrigen = ciudadOrigen;
	}
	public Integer getDestino() {
		return destino;
	}
	public void setDestino(Integer destino) {
		this.destino = destino;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public BigDecimal getImporteFlete() {
		return importeFlete;
	}
	public void setImporteFlete(BigDecimal importeFlete) {
		this.importeFlete = importeFlete;
	}
	public BigDecimal getImporteSeguro() {
		return importeSeguro;
	}
	public void setImporteSeguro(BigDecimal importeSeguro) {
		this.importeSeguro = importeSeguro;
	}
	public BigDecimal getValorFOB() {
		return valorFOB;
	}
	public void setValorFOB(BigDecimal valorFOB) {
		this.valorFOB = valorFOB;
	}
	public BigDecimal getPorcentageDescuento() {
		return porcentageDescuento;
	}
	public void setPorcentageDescuento(BigDecimal porcentageDescuento) {
		this.porcentageDescuento = porcentageDescuento;
	}
	public BigDecimal getImporteDescuento() {
		return importeDescuento;
	}
	public void setImporteDescuento(BigDecimal importeDescuento) {
		this.importeDescuento = importeDescuento;
	}
	public BigDecimal getPorcentageIGV() {
		return porcentageIGV;
	}
	public void setPorcentageIGV(BigDecimal porcentageIGV) {
		this.porcentageIGV = porcentageIGV;
	}
	public BigDecimal getImporteIGV() {
		return importeIGV;
	}
	public void setImporteIGV(BigDecimal importeIGV) {
		this.importeIGV = importeIGV;
	}
	public BigDecimal getImporteNeto() {
		return importeNeto;
	}
	public void setImporteNeto(BigDecimal importeNeto) {
		this.importeNeto = importeNeto;
	}
	public BigDecimal getImportePrecioBase() {
		return importePrecioBase;
	}
	public void setImportePrecioBase(BigDecimal importePrecioBase) {
		this.importePrecioBase = importePrecioBase;
	}
	public BigDecimal getImporteSoles() {
		return importeSoles;
	}
	public void setImporteSoles(BigDecimal importeSoles) {
		this.importeSoles = importeSoles;
	}
	public BigDecimal getImporteDolares() {
		return importeDolares;
	}
	public void setImporteDolares(BigDecimal importeDolares) {
		this.importeDolares = importeDolares;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public BigDecimal getSaldoAnticipo() {
		return saldoAnticipo;
	}
	public void setSaldoAnticipo(BigDecimal saldoAnticipo) {
		this.saldoAnticipo = saldoAnticipo;
	}
	public BigDecimal getTipoCambioEmision() {
		return tipoCambioEmision;
	}
	public void setTipoCambioEmision(BigDecimal tipoCambioEmision) {
		this.tipoCambioEmision = tipoCambioEmision;
	}
	public Integer getTransportista() {
		return transportista;
	}
	public void setTransportista(Integer transportista) {
		this.transportista = transportista;
	}
	public String getTransporte() {
		return transporte;
	}
	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}
	public Long getComisionista() {
		return comisionista;
	}
	public void setComisionista(Long comisionista) {
		this.comisionista = comisionista;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getObservaItems() {
		return observaItems;
	}
	public void setObservaItems(String observaItems) {
		this.observaItems = observaItems;
	}
	public String getUsuarioCreacion() {
		return usuarioCreacion;
	}
	public void setUsuarioCreacion(String usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}
	public Integer getTipoFactura() {
		return tipoFactura;
	}
	public void setTipoFactura(Integer tipoFactura) {
		this.tipoFactura = tipoFactura;
	}
	public BigDecimal getTasaIGV() {
		return tasaIGV;
	}
	public void setTasaIGV(BigDecimal tasaIGV) {
		this.tasaIGV = tasaIGV;
	}
	public Integer getTipoNotaCredito() {
		return tipoNotaCredito;
	}
	public void setTipoNotaCredito(Integer tipoNotaCredito) {
		this.tipoNotaCredito = tipoNotaCredito;
	}
	public Integer getTipoNotaDebido() {
		return tipoNotaDebido;
	}
	public void setTipoNotaDebido(Integer tipoNotaDebido) {
		this.tipoNotaDebido = tipoNotaDebido;
	}
	public String getSustento() {
		return sustento;
	}
	public void setSustento(String sustento) {
		this.sustento = sustento;
	}
	public String getIdentificacionTipo() {
		return identificacionTipo;
	}
	public void setIdentificacionTipo(String identificacionTipo) {
		this.identificacionTipo = identificacionTipo;
	}
	public String getIdentificacionNumero() {
		return identificacionNumero;
	}
	public void setIdentificacionNumero(String identificacionNumero) {
		this.identificacionNumero = identificacionNumero;
	}
	public Long getIdVendedor() {
		return idVendedor;
	}
	public void setIdVendedor(Long idVendedor) {
		this.idVendedor = idVendedor;
	}
	public Integer getTipoMuestra() {
		return tipoMuestra;
	}
	public void setTipoMuestra(Integer tipoMuestra) {
		this.tipoMuestra = tipoMuestra;
	}
}
