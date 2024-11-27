package pe.facele.michell.bean;

import java.math.BigDecimal;

public class ItemRetailBEAN {
	Integer item;
	Integer codigo;
	Integer clase;
	String descripcion;
	String caracteristica;
	String material;
	String composicion;
	String unidad;
	BigDecimal cantidad;
	BigDecimal porcentajeDescuento;
	BigDecimal precioUnitario;
	BigDecimal precioDescuentoUnitario;
	BigDecimal precioTotal;
	BigDecimal valorUnitario;
	BigDecimal valorDescuentoUnitario;
	BigDecimal valorTotal;
	BigDecimal importeIGVTotal;
	Integer grupoSerie;
	Integer grupoNumero;
	
	public Integer getItem() {
		return item;
	}
	public void setItem(Integer item) {
		this.item = item;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Integer getClase() {
		return clase;
	}
	public void setClase(Integer clase) {
		this.clase = clase;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getUnidad() {
		return unidad;
	}
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	public BigDecimal getCantidad() {
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public Integer getGrupoSerie() {
		return grupoSerie;
	}
	public void setGrupoSerie(Integer grupoSerie) {
		this.grupoSerie = grupoSerie;
	}
	public Integer getGrupoNumero() {
		return grupoNumero;
	}
	public void setGrupoNumero(Integer grupoNumero) {
		this.grupoNumero = grupoNumero;
	}
	public BigDecimal getPrecioDescuentoUnitario() {
		return precioDescuentoUnitario;
	}
	public void setPrecioDescuentoUnitario(BigDecimal precioDescuentoUnitario) {
		this.precioDescuentoUnitario = precioDescuentoUnitario;
	}
	public BigDecimal getValorDescuentoUnitario() {
		return valorDescuentoUnitario;
	}
	public void setValorDescuentoUnitario(BigDecimal valorDescuentoUnitario) {
		this.valorDescuentoUnitario = valorDescuentoUnitario;
	}
	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}
	public void setPrecioTotal(BigDecimal precioTotalNeto) {
		this.precioTotal = precioTotalNeto;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotalNeto) {
		this.valorTotal = valorTotalNeto;
	}
	public BigDecimal getPorcentajeDescuento() {
		return porcentajeDescuento;
	}
	public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
		this.porcentajeDescuento = porcentajeDescuento;
	}
	public String getCaracteristica() {
		return caracteristica;
	}
	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getComposicion() {
		return composicion;
	}
	public void setComposicion(String composicion) {
		this.composicion = composicion;
	}
	public BigDecimal getImporteIGVTotal() {
		return importeIGVTotal;
	}
	public void setImporteIGVTotal(BigDecimal importeIGVTotal) {
		this.importeIGVTotal = importeIGVTotal;
	}
}
