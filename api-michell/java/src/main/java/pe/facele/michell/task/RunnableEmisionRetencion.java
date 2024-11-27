package pe.facele.michell.task;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import pe.facele.docele.dol.services.emisionservice.Declarar;
import pe.facele.docele.dol.services.emisionservice.DeclararResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoDeclararType;
import pe.facele.docele.dol.services.emisionservice.FormatoObtenerType;
import pe.facele.docele.dol.services.emisionservice.Obtener;
import pe.facele.docele.dol.services.emisionservice.ObtenerResponse;
import pe.facele.docele.schemas.v11.comprobantevinculado.CREType;
import pe.facele.docele.schemas.v11.comprobantevinculado.ComprobanteVinculado;
import pe.facele.docele.schemas.v11.comprobantevinculado.ComprobanteRetencionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.DatosRetencionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.DomicilioFiscalType;
import pe.facele.docele.schemas.v11.comprobantevinculado.EmisorType;
import pe.facele.docele.schemas.v11.comprobantevinculado.IdComprobanteType;
import pe.facele.docele.schemas.v11.comprobantevinculado.ObjectFactory;
import pe.facele.docele.schemas.v11.comprobantevinculado.ObservacionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.ReceptorType;
import pe.facele.docele.schemas.v11.comprobantevinculado.RegimenRetencionType;
import pe.facele.docele.schemas.v11.comprobantevinculado.TipoComprobanteType;
import pe.facele.docele.schemas.v11.comprobantevinculado.TipoIdentidadType;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.FormatoFecha;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.MichelException;
import pe.facele.michell.api.Resource;
import pe.facele.michell.bean.CorrientistaBEAN;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.RetencionBEAN;
import pe.facele.michell.bean.RetencionDetalleBEAN;
import pe.facele.michell.bean.SerieBEAN;

public class RunnableEmisionRetencion implements Runnable {
	Logger logger = Logger.getLogger(this.getClass());

	private IdProceso bean;

	public RunnableEmisionRetencion(IdProceso bean) {
		this.bean = bean;
	}

	@Override
	public void run() {
		logger.info("CorrelativoInterno[" + bean.getNumeroInterno().toString()
				+ "] y Correlativo Comprobante[" + bean.getCorrelativoComprobante().toString()
				+ "]");
		Connection con = null;
		DAO dao;
		CREType creType;
		ComprobanteRetencionType comprobante;
		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);

			RetencionBEAN encabezadoRetencion = dao.getEncabezadoRetencion(bean);
			SerieBEAN serie = dao.getSerie(bean);
			
			OrganizacionBEAN organizacion = dao.getOrganizacion(serie.getIdOrganizacion());
			if (serie.getIdOrganizacion().compareTo(bean.getOrganizacionID()) != 0) {
				logger.debug("No corresponde al RUC");
				return;
			}
			CorrientistaBEAN corrientista = dao.getCorrientista(encabezadoRetencion.getIdCorrientista().longValue());
			
			creType = new CREType();

			IdComprobanteType idComprobante = new IdComprobanteType();
			
			idComprobante.setSerie(serie.getSerie());
			
			idComprobante.setCorrelativo(new BigInteger(bean.getCorrelativoComprobante().toString()));
			idComprobante.setFechaEmision(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(encabezadoRetencion.getFechaEmision())));
			creType.setIdComprobante(idComprobante );
			
			EmisorType emisor = new EmisorType();
			emisor.setRuc(new BigInteger(organizacion.getRuc()));
			emisor.setRazonSocial(organizacion.getDescripcion());
			emisor.setNombreComercial(organizacion.getDescripcion());
			
			DomicilioFiscalType domiciolioFiscal = new DomicilioFiscalType();
			domiciolioFiscal.setUbigeo(BigInteger.valueOf(Resource.getUBIGEO(serie.getIdOrganizacion())));
			domiciolioFiscal.setDepartamento(organizacion.getProvincia());
			domiciolioFiscal.setDistrito(organizacion.getDistrito());
			domiciolioFiscal.setProvincia(organizacion.getProvincia());
			domiciolioFiscal.setUrbanizacion(organizacion.getZona());
			domiciolioFiscal.setDireccion("AV. " + organizacion.getNombreVia() + " " +  organizacion.getNumeroVia());
			emisor.setDomicilio(domiciolioFiscal);
			
			creType.setEmisor(emisor );
			
			ReceptorType proveedor = new ReceptorType();
			proveedor.setTipoIdentificacion(TipoIdentidadType.RUC);
			proveedor.setNumero(corrientista.getIdentificacionNumero());
			proveedor.setNombre(corrientista.getNombre());
			creType.setProveedor(proveedor);
			
			List<RetencionDetalleBEAN> comprobantes = dao.getDetalleRetenciones(bean);
			if (comprobantes.size() < 1) {
				logger.info("Error: no hay comprobantes asociados a retencion");
				dao.updateRetencionEstado(bean, Constantes.MICHELL_ESTADO_RETENCION.RECHAZADO.getCode());
				con.commit();
				return;
			}
			
			BigDecimal _importeRetenidoTotal = BigDecimal.valueOf(0);
			BigDecimal _importePagadoTotal = BigDecimal.valueOf(0);
			BigDecimal factorCambioCR =  BigDecimal.valueOf(0);
			RetencionDetalleBEAN nc = null;
			for (RetencionDetalleBEAN comp : comprobantes) {
				if (Constantes.SUNAT_TIPO_DOCUMENTO.fromValue(comp.getTipoComprobante()).equals(Constantes.SUNAT_TIPO_DOCUMENTO.NOTA_CREDITO)) {
					nc = comp;
					continue;
				} else if (nc != null) {
					comp.setImporteTotal(comp.getImporteTotal().subtract(nc.getImporteTotal()));
					comp.setImportePagadoBrutoPEN(comp.getImportePagadoBrutoPEN().subtract(nc.getImportePagadoBrutoPEN()));
					comp.setImporteRetenidoPEN(comp.getImporteRetenidoPEN().subtract(nc.getImporteRetenidoPEN()));
				}
				
				comprobante = new ComprobanteRetencionType();
				comprobante.setTipoComprobanteCR(TipoComprobanteType.valueOf(Constantes.SUNAT_TIPO_DOCUMENTO.fromValue(comp.getTipoComprobante()).toString()));

				String sr = comp.getSerie();
				while (sr.length() < 4 && sr.matches("\\d*"))
					sr = "0" + sr;
				comprobante.setSerieCR(sr);
				comprobante.setCorrelativoCR(BigInteger.valueOf(comp.getCorrelativo()));
				comprobante.setFechaEmisionCR(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(comp.getFechaEmision())));
				comprobante.setMonedaCR(comp.getMoneda());
				comprobante.setImporteDocumentoCR(comp.getImporteTotal());
				
				if (!comp.getMoneda().equals(Constantes.MICHELL_TIPO_MONEDA.PEN.toString()) && encabezadoRetencion.getTasaCambio() != null)
					comprobante.setPagoImporteBrutoCR(comp.getImportePagadoBrutoPEN().divide(encabezadoRetencion.getTasaCambio(), 2, BigDecimal.ROUND_HALF_DOWN));
				else
					comprobante.setPagoImporteBrutoCR(comp.getImportePagadoBrutoPEN());
				
				if (encabezadoRetencion.getTasaCambio() !=  null && encabezadoRetencion.getTasaCambio().compareTo(factorCambioCR) == 1)
					factorCambioCR = encabezadoRetencion.getTasaCambio();
				comprobante.setFactorCambioCR(encabezadoRetencion.getTasaCambio());
				
				comprobante.setPagoFechaCR(DatatypeFactory.newInstance().newXMLGregorianCalendar(FormatoFecha.convertFecha(encabezadoRetencion.getFechaEmision())));
				comprobante.setPagoNumeroCR(encabezadoRetencion.getNumeroMovimientoCaja());
				
				comprobante.setImporteRetenidoPEN(comp.getImporteRetenidoPEN());
				comprobante.setImporteNetoPagadoPEN(comp.getImportePagadoBrutoPEN().subtract(comp.getImporteRetenidoPEN()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
				
				creType.getComprobanteRelacionado().add(comprobante);
				
				_importeRetenidoTotal = _importeRetenidoTotal.add(comprobante.getImporteRetenidoPEN());
				_importePagadoTotal = _importePagadoTotal.add(comprobante.getImporteNetoPagadoPEN());
				
				//Validacion SUNAT
				if (comprobante.getTipoComprobanteCR().equals(TipoComprobanteType.NOTA_CREDITO))
					throw new MichelException("Comprobante tipo '" +  TipoComprobanteType.NOTA_CREDITO.name()
							+ "' no puede ser informado en RETENCIONES.");
			}
			
			ObservacionType _observaciones;
			if (factorCambioCR.toString() != null) {
				_observaciones = new ObservacionType();
				_observaciones.setNombre(Resource.getString("KEY.FACTOR.CAMBIO"));
				_observaciones.setContenido(factorCambioCR.toString());
				creType.getObservacion().add(_observaciones);
			}

			if (encabezadoRetencion.getNumeroCheque() != null) {
				_observaciones = new ObservacionType();
				_observaciones.setNombre(Resource.getString("KEY.NUMERO.CHEQUE"));
				_observaciones.setContenido(encabezadoRetencion.getNumeroCheque());
				creType.getObservacion().add(_observaciones);
			}

			if (encabezadoRetencion.getAsientoContable() != null) {
				_observaciones = new ObservacionType();
				_observaciones.setNombre(Resource.getString("KEY.ASIENTO.CONTABLE"));
				_observaciones.setContenido(encabezadoRetencion.getAsientoContable());
				creType.getObservacion().add(_observaciones);
				
			}
			
			DatosRetencionType retencion = new DatosRetencionType();
		
			retencion.setCodigoRegimen(RegimenRetencionType.RegimenRetencionUno);
			retencion.setTasa(BigDecimal.valueOf(3));
			retencion.setImporteTotalNetoPagado(_importePagadoTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			retencion.setImporteTotalRetenido(_importeRetenidoTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			creType.setRetencion(retencion );

			ObjectFactory of = new ObjectFactory();
			ComprobanteVinculado retencionDocumento = of.createComprobanteVinculado();
			retencionDocumento.setCRE(creType);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JAXB.marshal(retencionDocumento, baos);
			baos.flush();
			encabezadoRetencion.setXml(baos.toByteArray());

//			logger.info("baos.toString():\n" + baos.toString());
//			Thread.sleep(60*60*1000);
			
			//Armando documento
			try {
				EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
				EmisionService port = service.getEmisionServicePort();
				
				Declarar parameters = new Declarar();
				parameters.setTipoDocumento("20");
				parameters.setFormato(FormatoDeclararType.XM_LV_1_1);
				parameters.setRucEmisor(organizacion.getRuc());
				parameters.setDocumento(baos.toString());
				
				final DeclararResponse response = port.declarar(parameters);
				JAXB.marshal(response, System.out);
				
				if (response.getReturn().getRespuesta().getEstado() == 2
						|| (response.getReturn().getRespuesta().getEstado() == 1 && response.getReturn().getRespuesta().getDescripcion().contains("El comprobante ya existe"))) {
					logger.info("Comprobante emitido.");

					Obtener parametersPDF = new Obtener();
					parametersPDF.setRucEmisor(emisor.getRuc().toString());
					parametersPDF.setTipoDocumento("20");
					parametersPDF.setSerie(idComprobante.getSerie());
					parametersPDF.setCorrelativo(idComprobante.getCorrelativo().toString());
					parametersPDF.setCantidad(1);
					parametersPDF.setFormato(FormatoObtenerType.PDF);
					
					ObtenerResponse responsePDF = port.obtener(parametersPDF);
					
					encabezadoRetencion.setEmisionElectronica(Constantes.MICHELL_ESTADO_RETENCION.EMITIDO.getCode());
					encabezadoRetencion.setPdf(responsePDF.getReturn().getPDF());
					
					//llamando xml

					parametersPDF.setTipoDocumento("20");
					parametersPDF.setRucEmisor(emisor.getRuc().toString());
					parametersPDF.setSerie(idComprobante.getSerie());
					parametersPDF.setCorrelativo(idComprobante.getCorrelativo().toString());
					parametersPDF.setTipoDocumento("20");
					parametersPDF.setCantidad(1);
					parametersPDF.setFormato(FormatoObtenerType.XML);
					
					responsePDF = port.obtener(parametersPDF);
					encabezadoRetencion.setXml(responsePDF.getReturn().getXML().getBytes());

					dao.insertPDF(encabezadoRetencion);
					con.commit();
					
				} else if (response.getReturn().getRespuesta().getEstado() == 1) {
					logger.warn(response.getReturn().getRespuesta().getDescripcion());
					dao.updateRetencionEstado(bean,Constantes.MICHELL_ESTADO_RETENCION.RECHAZADO.getCode());
					con.commit();
					
				} else
					throw new Exception(response.getReturn().getRespuesta().getDescripcion());
			
			} catch (MichelException me) {
				logger.info("Corregir Retencion: " + me.getMessage(), me);
				dao.updateRetencionEstado(bean,Constantes.MICHELL_ESTADO_RETENCION.RECHAZADO.getCode());
				con.commit();
			
			} catch (Exception e2) {
				con.rollback();
				logger.error(e2.getMessage(), e2);
			}
	
			
		} catch (Exception e) {
			logger.error("Error con CorrelativoInterno[" + bean.getNumeroInterno().toString()
					+ "] y Numero Factura[" + bean.getCorrelativoComprobante().toString()
					+ "]: " + e.getMessage(), e);
			
		} finally {
			DataSourceFactory.desconectar(con);
			Constantes.TURNO.remove(bean.getOrganizacionID() + bean.getNumeroInterno().toString() + bean.getCorrelativoComprobante().toString());
		}

	}
}
