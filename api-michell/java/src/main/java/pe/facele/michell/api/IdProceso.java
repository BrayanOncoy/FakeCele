package pe.facele.michell.api;

import java.util.Date;

public class IdProceso {

    Long numeroInterno;
    Long correlativoComprobante;
    Integer organizacionID;
    Date fechaEmision;
//    String RUC;
    String hash;
    String xml;
    String rutaXML;
    String rutaPDF;
    String rutaPDF417;
    byte[] iPDF417;
    byte[] pdf;
    Integer estado;

    public Long getNumeroInterno() {
        return numeroInterno;
    }

    public void setNumeroInterno(Long numeroInterno) {
        this.numeroInterno = numeroInterno;
    }

    public Long getCorrelativoComprobante() {
        return correlativoComprobante;
    }

    public void setCorrelativoComprobante(Long numeroFactura) {
        this.correlativoComprobante = numeroFactura;
    }

//    public String getRUC() {
//        return RUC;
//    }
//
//    public void setRUC(String rUC) {
//        RUC = rUC;
//    }

    public Integer getOrganizacionID() {
		return organizacionID;
	}

	public void setOrganizacionID(Integer organizacionID) {
		this.organizacionID = organizacionID;
	}

	public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public byte[] getiPDF417() {
        return iPDF417;
    }

    public void setiPDF417(byte[] iPDF417) {
        this.iPDF417 = iPDF417;
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

    public String getRutaXML() {
        return rutaXML;
    }

    public void setRutaXML(String rutaXML) {
        this.rutaXML = rutaXML;
    }

    public String getRutaPDF() {
        return rutaPDF;
    }

    public void setRutaPDF(String rutaPDF) {
        this.rutaPDF = rutaPDF;
    }

    public String getRutaPDF417() {
        return rutaPDF417;
    }

    public void setRutaPDF417(String rutaPDF417) {
        this.rutaPDF417 = rutaPDF417;
    }

}
