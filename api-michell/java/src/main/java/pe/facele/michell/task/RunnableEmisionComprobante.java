package pe.facele.michell.task;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import com.itextpdf.text.pdf.BarcodeQRCode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.GregorianCalendar;

import pe.facele.docele.dol.services.emisionservice.Declarar;
import pe.facele.docele.dol.services.emisionservice.DeclararResponse;
import pe.facele.docele.dol.services.emisionservice.EmisionService;
import pe.facele.docele.dol.services.emisionservice.EmisionService_Service;
import pe.facele.docele.dol.services.emisionservice.FormatoDeclararType;
import pe.facele.docele.dol.services.emisionservice.FormatoObtenerType;
import pe.facele.docele.dol.services.emisionservice.Obtener;
import pe.facele.docele.dol.services.emisionservice.ObtenerResponse;
import pe.facele.docele.schemas.v11.comprobante.AnticiposType;
import pe.facele.docele.schemas.v11.comprobante.CPEType;
import pe.facele.docele.schemas.v11.comprobante.CodigoDescuentoGlobalType;
import pe.facele.docele.schemas.v11.comprobante.CodigoDescuentoItemType;
import pe.facele.docele.schemas.v11.comprobante.CodigoFormaPagoType;
import pe.facele.docele.schemas.v11.comprobante.CodigoTipoNotaCreditoType;
import pe.facele.docele.schemas.v11.comprobante.CodigoTipoNotaDebitoType;
import pe.facele.docele.schemas.v11.comprobante.Comprobante;
import pe.facele.docele.schemas.v11.comprobante.CondicionesType;
import pe.facele.docele.schemas.v11.comprobante.ContraparteType;
import pe.facele.docele.schemas.v11.comprobante.CuotaType;
import pe.facele.docele.schemas.v11.comprobante.DescuentoGlobalType;
import pe.facele.docele.schemas.v11.comprobante.DescuentoItemType;
import pe.facele.docele.schemas.v11.comprobante.DetalleType;
import pe.facele.docele.schemas.v11.comprobante.DireccionPostalType;
import pe.facele.docele.schemas.v11.comprobante.EmisorType;
import pe.facele.docele.schemas.v11.comprobante.FormaPagoType;
import pe.facele.docele.schemas.v11.comprobante.IgvType;
import pe.facele.docele.schemas.v11.comprobante.ImpuestoType;
import pe.facele.docele.schemas.v11.comprobante.MonedaType;
import pe.facele.docele.schemas.v11.comprobante.ObjectFactory;
import pe.facele.docele.schemas.v11.comprobante.ObservacionType;
import pe.facele.docele.schemas.v11.comprobante.ReferenciaComercialType;
import pe.facele.docele.schemas.v11.comprobante.TipoIdentidadType;
import pe.facele.docele.schemas.v11.comprobante.TipoOperacionType;
import pe.facele.docele.schemas.v11.comprobante.TotalImpuestoType;
import pe.facele.docele.schemas.v11.comprobante.TotalesType;
import pe.facele.michell.api.Constantes;
import pe.facele.michell.api.DAO;
import pe.facele.michell.api.DataSourceFactory;
import pe.facele.michell.api.FormatoFecha;
import pe.facele.michell.api.IdProceso;
import pe.facele.michell.api.Resource;
import pe.facele.michell.api.Constantes.MICHELL_TIPO_DOCUMENTO;
import pe.facele.michell.api.Constantes.SUNAT_TIPO_DOCUMENTO;
import pe.facele.michell.bean.AnticipoBEAN;
import pe.facele.michell.bean.CorrientistaBEAN;
import pe.facele.michell.bean.DetalleBEAN;
import pe.facele.michell.bean.EncabezadoBEAN;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.ReferenciaBEAN;
import pe.facele.michell.bean.SerieBEAN;
import pe.facele.michell.bean.TotalesBEAN;

public class RunnableEmisionComprobante implements Runnable {

	Logger logger = Logger.getLogger(this.getClass());

	private IdProceso bean;

	private Formatter obj;

	public RunnableEmisionComprobante(IdProceso bean) {
		this.bean = bean;
	}

	@Override
	public void run() {
		/*
		 * 1. obtener la data segun modelo negocio 2. generar documento 3. commit 4.
		 * confirmar documento 5. entregar documento
		 */
		logger.info(
				"\n\t\t\t\t\t--\n\t\t\t\t\tNueva transaccion CorrelativoInterno[" + bean.getNumeroInterno().toString()
						+ "] y NumeroComprobante[" + bean.getCorrelativoComprobante().toString() + "]");

		Connection con = null;
		DAO dao = null;
		String glosaCambio = null;
		String incoterm = null;
		String observacionAduana = null;
		DescuentoGlobalType descuentoGlobal = null;

		try {
			con = DataSourceFactory.getConnection();
			dao = new DAO(con);

			SerieBEAN serie = dao.getSerie(bean);

			if (serie.getIdOrganizacion().compareTo(bean.getOrganizacionID()) != 0)
				return;

			logger.info(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento()).name());

			OrganizacionBEAN organizacion = dao.getOrganizacion(serie.getIdOrganizacion());

			if (organizacion == null) {
				return;
			}

			EncabezadoBEAN encabezado = dao.getEncabezado(bean);

			TotalesBEAN totalesBEAN = dao.getTotales(bean);

			CorrientistaBEAN corrientista;
			if (encabezado.getIdCorrientista() != null) {
				corrientista = dao.getCorrientista(encabezado.getIdCorrientista());
				if (corrientista.getDireccion() != null && corrientista.getDireccion().contains("\n")) {
					corrientista.setDireccion(corrientista.getDireccion().replace("\n", " "));
				}

			} else {
				corrientista = new CorrientistaBEAN();
				corrientista.setIdentificacionTipo(Constantes.SUNAT_TIPO_IDENTIFICACION.NINGUNO.getCode());
			}

			CorrientistaBEAN vendedor = null;
			if (encabezado.getIdVendedor() != null) {
				vendedor = dao.getCorrientista(encabezado.getIdVendedor());
			}

			OrganizacionBEAN sucursal = null;
			if (encabezado.getOrigen() != null) {
				sucursal = dao.getOrganizacion(encabezado.getOrigen());
			}

			List<AnticipoBEAN> anticipoBEAN = dao.getAnticipos(bean);

			List<DetalleBEAN> detalles = dao.getDetalleComprobante(bean);
			if (detalles.size() == 0) {
				throw new Exception("No tiene detalles asociados el comprobante");
			}

			// Normaliza datos de identificacion de Adquiriente
			DetalleBEAN primerDetalle = detalles.get(0);
			if (primerDetalle.getTipoIdentificacionAdquiriente() != null)
				corrientista.setIdentificacionTipo(primerDetalle.getTipoIdentificacionAdquiriente());

			if (primerDetalle.getNumeroIdentificacionAdquiriente() != null)
				corrientista.setIdentificacionNumero(primerDetalle.getNumeroIdentificacionAdquiriente());

			if (primerDetalle.getNombreAdquiriente() != null)
				corrientista.setNombre(primerDetalle.getNombreAdquiriente().replace("\n", " "));

			if (primerDetalle.getDireccionAdquiriente() != null)
				corrientista.setDireccion(detalles.get(0).getDireccionAdquiriente().replace("\n", " "));

			List<ReferenciaBEAN> referencias;
			if (Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
					.equals(Constantes.MICHELL_TIPO_DOCUMENTO.NOTA_CREDITO)) {
				referencias = dao.getReferenciasNota(bean);
			} else if (Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
					.equals(Constantes.MICHELL_TIPO_DOCUMENTO.NOTA_DEBITO)) {
				referencias = dao.getReferenciasNota(bean);
			} else {
				referencias = dao.getReferencias(bean);
			}

			glosaCambio = dao.getGlosaCambio(bean);

			ObjectFactory of = new ObjectFactory();
			EmisorType emisorT = of.createEmisorType();
			emisorT.setRuc(organizacion.getRuc());
			emisorT.setRazonSocial(organizacion.getDescripcion());
			emisorT.setNombreComercial(organizacion.getDescripcion());
			emisorT.setCodigoEstablecimientoSUNAT("0000");

			DireccionPostalType domicilioFiscal = of.createDireccionPostalType();
			domicilioFiscal.setUbigeo(Resource.getUBIGEO(bean.getOrganizacionID()) + "");
			domicilioFiscal.setDireccionCompleta(organizacion.getDireccion());
			domicilioFiscal.setUrbanizacion(organizacion.getDistrito());
			domicilioFiscal.setDistrito(organizacion.getDistrito());
			domicilioFiscal.setProvincia(organizacion.getProvincia());
			domicilioFiscal.setDepartamento(organizacion.getDistrito());
			emisorT.setDomicilioFiscal(domicilioFiscal);

			CondicionesType condiciones = of.createCondicionesType();
			if (encabezado.getFechaEmision() != null) {
				condiciones.setFechaEmision(DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(FormatoFecha.convertFecha(encabezado.getFechaEmision())));
			} else {
				throw new Exception("Error con Numero Interno: " + bean.getNumeroInterno() + "Numero Comprobante: "
						+ bean.getCorrelativoComprobante() + "La fecha de emision es obligatoria");
			}
			if (encabezado.getMoneda() != null) {
				condiciones.setMoneda(MonedaType.fromValue(encabezado.getMoneda()));
			} else {
				throw new Exception("Error con Numero Interno: " + bean.getNumeroInterno() + "Numero Comprobante: "
						+ bean.getCorrelativoComprobante() + "La moneda es obligatoria");
			}

			condiciones.setTerminosPago(encabezado.getFormaPago());
			if (encabezado.getFormaPago() != null && !encabezado.getFormaPago().equals("CONTADO")
					&& encabezado.getFechaVencimiento() != null) {
				condiciones.setFechaVencimiento(DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(FormatoFecha.convertFecha(encabezado.getFechaVencimiento())));
			}

			if (Constantes.MICHELL_TIPO_FACTURA.ANTICIPO.getCode() == encabezado.getTipoFactura().intValue()) {
				logger.info(Constantes.MICHELL_TIPO_FACTURA.ANTICIPO);
				condiciones.setTipoOperacion(TipoOperacionType.Ventalnterna);

			} else if (Constantes.MICHELL_TIPO_FACTURA.EXPORTACION.getCode() == encabezado.getTipoFactura()
					.intValue()) {
				condiciones.setTipoOperacion(TipoOperacionType.ExportacionBienes);

			} else if ((Constantes.MICHELL_TIPO_FACTURA.ANTICIPO.getCode() != encabezado.getTipoFactura().intValue()
					&& Constantes.MICHELL_TIPO_FACTURA.EXPORTACION.getCode() != encabezado.getTipoFactura()
							.intValue())) {
				condiciones.setTipoOperacion(TipoOperacionType.Ventalnterna);
			}

			if (Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
					.equals(Constantes.MICHELL_TIPO_DOCUMENTO.NOTA_CREDITO)) {
				condiciones.setNotaCreditoTipo(CodigoTipoNotaCreditoType
						.fromValue(String.format("%02d", referencias.get(0).getCodigoSunat())));
				condiciones.setNotaSustento(referencias.get(0).getSustento());

			} else if (Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
					.equals(Constantes.MICHELL_TIPO_DOCUMENTO.NOTA_DEBITO)) {
				condiciones.setNotaDebitoTipo(
						CodigoTipoNotaDebitoType.fromValue(String.format("%02d", referencias.get(0).getCodigoSunat())));
				condiciones.setNotaSustento(referencias.get(0).getSustento());

			}

			CPEType cpe = of.createCPEType();

			BigDecimal totalCantidad = BigDecimal.valueOf(0);
			BigDecimal totalUnidades = BigDecimal.valueOf(0);
			DetalleType detalle;

			BigDecimal descuentoOtros = BigDecimal.valueOf(0);
			String descrpcionUTF8;
			ByteBuffer uniBuf;
			CharBuffer charBuf = null;
			CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
			decoder.onMalformedInput(CodingErrorAction.REPLACE);
			decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
			FormaPagoType formaPago = new FormaPagoType();
			CuotaType cuota = new CuotaType();

			for (DetalleBEAN b : detalles) {
				detalle = new DetalleType();

				if (b.getUnidadMedida() == null || b.getUnidadMedida().equals("UNI")) {
					detalle.setUnidadMedida("NIU");
				} else {
					detalle.setUnidadMedida(b.getUnidadMedida());
				}
				if (b.getCantidad() != null) {
					detalle.setCantidadUnidades(b.getCantidad());
				} else {
					throw new Exception("La cantidad es un dato obligatorio [" + b.getDescripcion() + "]");
				}

				if (b.getCodigo() != null) {
					detalle.setCodigo(b.getCodigo().replace("\n", ""));
				} else {
					throw new Exception("Codigo no informado para producto [" + b.getDescripcion() + "]");
				}

				descrpcionUTF8 = b.getDescripcion().replaceAll("\n", "");
				uniBuf = ByteBuffer.wrap(descrpcionUTF8.getBytes());
				try {
					charBuf = decoder.decode(uniBuf);
					descrpcionUTF8 = charBuf.toString();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				if (b.getDescripcion() != null) {
					detalle.setDescripcion(descrpcionUTF8);
				} else {
					throw new Exception("Descripcion no informada para [" + b.getCodigo() + "]");
				}

				if (b.getValorUnitario() != null) {
					detalle.setValorUnitario(b.getValorUnitario());
				} else {
					throw new Exception("El valor unitario es obligatorio");
				}

				if (b.getPrecioUnitario() != null) {
					detalle.setPrecioUnitario(b.getPrecioUnitario());
				} else {
					throw new Exception("ERR_01: El Precio Unitario es obligatorio");
				}

				if (b.getIgvMonto() != null) {
					detalle.setIgvMonto(b.getIgvMonto());
				} else {
					throw new Exception("El IGV es obligatorio");
				}

				if (b.getIgvCodigo().intValue() == Constantes.SUNAT_CODIGOS_AFECTACION_IGV.GRAVADO_BONIFICACION
						.getCode()) {
					descuentoOtros = descuentoOtros.add(b.getValorItem().add(b.getIgvMonto()));
					detalle.setIgvTipo(IgvType.Gravado_OperacionOnerosa);

				} else {
					detalle.setIgvTipo(IgvType.fromValue(String
							.valueOf(Constantes.SUNAT_CODIGOS_AFECTACION_IGV.fromValue(b.getIgvCodigo()).getCode())));
					descuentoOtros = b.getDescuentoGlobal();
				}
				if (b.getDescuento() != null && b.getDescuento().compareTo(BigDecimal.ZERO) == 1) {

					DescuentoItemType descuento = new DescuentoItemType();
					descuento.setCodigo(CodigoDescuentoItemType.AfectaBaseImponible);
					descuento.setValor(b.getDescuento());
					descuento.setMontoBase(b.getDescuento().add(b.getValorItem()));
					descuento.setFactor(
							descuento.getValor().divide(descuento.getMontoBase(), 4, BigDecimal.ROUND_HALF_EVEN));
					detalle.getDescuento().add(descuento);
				}
				detalle.setValorItem(b.getValorItem());
				detalle.setCodigoSUNAT(Resource.getCODIGO_EXPORTACION_SUNAT(serie.getIdOrganizacion()));
				cpe.getDetalle().add(detalle);

				if (Constantes.SUNAT_CODIGOS_AFECTACION_IGV.EXPORTACION.getCode() == b.getIgvCodigo()) {
					condiciones.setTipoOperacion(TipoOperacionType.ExportacionBienes);
				}

				totalCantidad = totalCantidad.add(detalle.getCantidadUnidades());

				if (b.getSubCantidad() != null) {
					totalUnidades = totalUnidades.add(b.getSubCantidad());
				}

				// se rescata forma de pago porque los simpaticos lo enviaron en el DETALLE
				if (b.getFormaPago() != null) {
					formaPago.setTipo(CodigoFormaPagoType.fromValue(b.getFormaPago()));
					if (formaPago.getTipo().equals(CodigoFormaPagoType.Credito)
							|| formaPago.getTipo().equals(CodigoFormaPagoType.CREDITO)) {
						formaPago.setMontoNeto(b.getMontoNetoPago());
						cuota.setFechaVencimientoCuota(toXMLDate(b.getFechaVencimientoCuota()));
						cuota.setMontoCuota(b.getMontoCuotaPago());
						cuota.setNumero(b.getNumeroCuota());
					}
				}
			}

			if (formaPago.getTipo() != null) {
				if (formaPago.getTipo().equals(CodigoFormaPagoType.Credito)
						|| formaPago.getTipo().equals(CodigoFormaPagoType.CREDITO))
					formaPago.getCuota().add(cuota);
				condiciones.setFormaPago(formaPago);
			}
			ContraparteType receptorT = of.createContraparteType();

			receptorT.setTipoIdentificacion(TipoIdentidadType.fromValue(corrientista.getIdentificacionTipo()));

			receptorT.setNumeroIdentificacion(corrientista.getIdentificacionNumero());

			if (corrientista.getNombre() != null) {
				receptorT.setNombre(corrientista.getNombre());
			} else {
				receptorT.setNombre("-");
			}

			if (corrientista.getDireccion() != null) {
				receptorT.setDireccionFisica(corrientista.getDireccion());
			}

			if (receptorT.getNumeroIdentificacion() == null)
				receptorT.setNumeroIdentificacion("-");
			else
				receptorT
						.setNumeroIdentificacion(receptorT.getNumeroIdentificacion().replace("*", "").replace("?", ""));

//			PARCHES MICHELL :(
			if (receptorT.getTipoIdentificacion().compareTo(TipoIdentidadType.Ninguno) == 0
					&& condiciones.getTipoOperacion() != null
					&& condiciones.getTipoOperacion().compareTo(TipoOperacionType.ExportacionBienes) == 0
					&& receptorT.getNumeroIdentificacion() != null
					&& receptorT.getNumeroIdentificacion().length() > 2) {
				receptorT.setTipoIdentificacion(TipoIdentidadType.PersonaJuridicaIdentificationNumberIN);
			}

			cpe.setTipoComprobante(Constantes.mapTipoDocumentoMichell
					.get(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())));
			cpe.setSerie(serie.getSerie());
			cpe.setCorrelativo(bean.getCorrelativoComprobante().intValue());
			cpe.setEmisor(emisorT);
			cpe.setAdquiriente(receptorT);
			cpe.setCondiciones(condiciones);

			if (anticipoBEAN != null) {

				AnticiposType anticipo = of.createAnticiposType();

				for (AnticipoBEAN anti : anticipoBEAN) {

					anticipo = new AnticiposType();
					anticipo.setRutEmisorDocumento(anti.getRucEmisior());
					anticipo.setTipoDocumento(SUNAT_TIPO_DOCUMENTO
							.valueOf(MICHELL_TIPO_DOCUMENTO.fromValue(anti.getTipoDocumentoMichell()).name())
							.getCode());
					anticipo.setSerieDocumento(anti.getSerie());
					anticipo.setCorrelativoDocumento(anti.getCorrelativo());
					anticipo.setImporteDocumento(anti.getImporteDocumento().multiply(BigDecimal.valueOf(1.18))
							.setScale(2, RoundingMode.HALF_UP));
					anticipo.setTotalAnticipos(anti.getImporteAcumulado().multiply(BigDecimal.valueOf(1.18)).setScale(2,
							RoundingMode.HALF_UP));

					cpe.getAnticipo().add(anticipo);

					descuentoGlobal = new DescuentoGlobalType();
					descuentoGlobal.setCodigo(CodigoDescuentoGlobalType.AnticiposGravados);
					descuentoGlobal.setMontoBase(anti.getImporteDocumento().add(encabezado.getImporteNeto()).setScale(2,
							RoundingMode.HALF_UP));
					descuentoGlobal.setFactor(
							anti.getImporteDocumento().divide(descuentoGlobal.getMontoBase(), 5, RoundingMode.HALF_UP));
					descuentoGlobal.setValor(anti.getImporteDocumento().setScale(2, RoundingMode.HALF_UP));

				}

			} else
				logger.info("No hay anticipo");

			if (condiciones.getTipoOperacion() != null
					&& condiciones.getTipoOperacion().equals(TipoOperacionType.ExportacionBienes)) {
				incoterm = dao.getIncoterm(bean);
				observacionAduana = dao.getObservacionAduana(bean);
				if (observacionAduana != null) {
					observacionAduana = observacionAduana.replace("\n", " ");
				}

				// inserta FLETE
				if (detalles.get(0).getFlete() != null
						&& detalles.get(0).getFlete().compareTo(BigDecimal.valueOf(0L)) == 1) {
					detalle = new DetalleType();
					detalle.setUnidadMedida("NIU");
					detalle.setCantidadUnidades(BigDecimal.ONE);
					detalle.setCodigo("FLETE/FREIGHT");
					detalle.setDescripcion("FLETE/FREIGHT");
					detalle.setIgvTipo(IgvType.ExportacionBienesServicios);
					detalle.setIgvMonto(BigDecimal.ZERO);
					detalle.setValorUnitario(detalles.get(0).getFlete());
					detalle.setPrecioUnitario(detalles.get(0).getFlete());
					detalle.setValorItem(detalles.get(0).getFlete());
					detalle.setCodigoSUNAT(Resource.getCODIGO_EXPORTACION_SUNAT(serie.getIdOrganizacion()));
					cpe.getDetalle().add(detalle);
				}

				// inserta SEGURO
				if (detalles.get(0).getSeguro() != null
						&& detalles.get(0).getSeguro().compareTo(BigDecimal.valueOf(0L)) == 1) {
					detalle = new DetalleType();
					detalle.setUnidadMedida("NIU");
					detalle.setCantidadUnidades(BigDecimal.ONE);
					detalle.setCodigo("SEGURO");
					detalle.setDescripcion("SEGURO");
					detalle.setIgvTipo(IgvType.ExportacionBienesServicios);
					detalle.setIgvMonto(BigDecimal.ZERO);
					detalle.setValorUnitario(detalles.get(0).getSeguro());
					detalle.setPrecioUnitario(detalles.get(0).getSeguro());
					detalle.setValorItem(detalles.get(0).getSeguro());
					detalle.setCodigoSUNAT(Resource.getCODIGO_EXPORTACION_SUNAT(serie.getIdOrganizacion()));
					cpe.getDetalle().add(detalle);
				}

				// inserta GASTOS FINANCIEROS
				if (detalles.get(0).getGastosFinancieros() != null
						&& detalles.get(0).getGastosFinancieros().compareTo(BigDecimal.valueOf(0L)) == 1) {
					detalle = new DetalleType();
					detalle.setUnidadMedida("NIU");
					detalle.setCantidadUnidades(BigDecimal.ONE);
					detalle.setCodigo("GTOS. FINANC.");
					detalle.setDescripcion("GASTOS FINANCIEROS");
					detalle.setIgvTipo(IgvType.ExportacionBienesServicios);
					detalle.setIgvMonto(BigDecimal.ZERO);
					detalle.setValorUnitario(detalles.get(0).getGastosFinancieros());
					detalle.setPrecioUnitario(detalles.get(0).getGastosFinancieros());
					detalle.setValorItem(detalles.get(0).getGastosFinancieros());
					detalle.setCodigoSUNAT(Resource.getCODIGO_EXPORTACION_SUNAT(serie.getIdOrganizacion()));
					cpe.getDetalle().add(detalle);
				}

			} else {
				// inserta FLETE
				if (detalles.get(0).getFlete() != null
						&& detalles.get(0).getFlete().compareTo(BigDecimal.valueOf(0L)) == 1) {
					detalle = new DetalleType();
					detalle.setUnidadMedida("NIU");
					detalle.setCantidadUnidades(BigDecimal.ONE);
					detalle.setCodigo("FLETE");
					detalle.setDescripcion("FLETE");
					detalle.setIgvTipo(IgvType.Gravado_OperacionOnerosa);
					detalle.setIgvMonto(detalles.get(0).getFlete().multiply(BigDecimal.valueOf(0.18)).setScale(4));
					detalle.setValorUnitario(detalles.get(0).getFlete());
					detalle.setPrecioUnitario(
							detalles.get(0).getFlete().multiply(BigDecimal.valueOf(1.18)).setScale(4));
					detalle.setValorItem(detalles.get(0).getFlete());
					cpe.getDetalle().add(detalle);
				}
			}

			BigDecimal totalSubtotal = BigDecimal.ZERO;
			BigDecimal totalExportadas = BigDecimal.ZERO;
			for (DetalleType _detalle : cpe.getDetalle()) {
				totalSubtotal = totalSubtotal.add(_detalle.getValorItem());

				if (_detalle.getIgvTipo().equals(IgvType.ExportacionBienesServicios))
					totalExportadas = totalExportadas.add(_detalle.getValorItem());

			}

			TotalesType totalesT = of.createTotalesType();
			if (bean.getOrganizacionID() == 100) {

				totalesT.setSubTotal(totalSubtotal);
				totalesT.setValorOperacionesGratuitas(totalesBEAN.getValorGratuito());
				totalesT.setValorOperacionesInafectas(totalesBEAN.getValorInafecto());
				totalesT.setValorOperacionesExoneradas(totalesBEAN.getValorExonerado());
				// BD DEL CLENTE NO ALMACENA VALOR DE FLETE. SE CALCULA RECORRIENDO LOS
				// DETALLES.
				totalesT.setValorOperacionesExportadas(totalExportadas);
				totalesT.setValorOperacionesGravadas(totalesBEAN.getValorGravado());

				if (descuentoGlobal != null)
					totalesT.getDescuentoGlobal().add(descuentoGlobal);

				if (!cpe.getAnticipo().isEmpty()) {
					for (AnticiposType anticipo : cpe.getAnticipo()) {

						// TODO DEBERIA SUMAR LOS ANTICIPOS?
//						totalesT.setValorTotalAnticipos(
//								totalesT.getValorTotalAnticipos().add(anticipo.getTotalAnticipos()));

						totalesT.setValorTotalAnticipos(anticipo.getTotalAnticipos());

					}
				}

			} else {

				totalesT.setSubTotal(totalSubtotal);
				totalesT.setValorOperacionesGratuitas(BigDecimal.ZERO);
				totalesT.setValorOperacionesInafectas(BigDecimal.ZERO);
				totalesT.setValorOperacionesExoneradas(BigDecimal.ZERO);

				if (descuentoGlobal != null)
					totalesT.getDescuentoGlobal().add(descuentoGlobal);

				if (!cpe.getAnticipo().isEmpty()) {
					for (AnticiposType anticipo : cpe.getAnticipo()) {

						// TODO DEBERIA SUMAR LOS ANTICIPOS?
//						totalesT.setValorTotalAnticipos(
//								totalesT.getValorTotalAnticipos().add(anticipo.getTotalAnticipos()));

						totalesT.setValorTotalAnticipos(anticipo.getTotalAnticipos());
					}
				}

				if (condiciones.getTipoOperacion() != null
						&& condiciones.getTipoOperacion().equals(TipoOperacionType.ExportacionBienes)) {
					totalesT.setValorOperacionesGravadas(BigDecimal.valueOf(0));
					totalesT.setValorOperacionesExportadas(encabezado.getImporteNeto());

				} else {
					totalesT.setValorOperacionesGravadas(encabezado.getImporteNeto());
					totalesT.setValorOperacionesExportadas(BigDecimal.valueOf(0));
				}
				if (condiciones.getTipoOperacion() != null
						&& condiciones.getTipoOperacion().equals(TipoOperacionType.ExportacionBienes)
						&& encabezado.getTipoMuestra() != null && encabezado.getTipoMuestra().intValue() == 1) {
					totalesT.setValorOperacionesGravadas(BigDecimal.valueOf(0));
					totalesT.setValorOperacionesExportadas(encabezado.getImporteNeto());
					totalesT.setValorOperacionesGratuitas(BigDecimal.valueOf(0));
				} else {
					totalesT.setValorOperacionesGratuitas(BigDecimal.valueOf(0));
				}
				totalesT.setValorOperacionesExoneradas(BigDecimal.valueOf(0));
			}

			TotalImpuestoType totalImpuestos = new TotalImpuestoType();
			totalImpuestos.setTipo(ImpuestoType.IGV);

			if (encabezado.getTasaIGV().compareTo(BigDecimal.ZERO) == 1)
				totalImpuestos.setFactor(encabezado.getTasaIGV());
			else
				totalImpuestos.setFactor(Constantes.TASA_IGV);

			totalImpuestos.setValor(encabezado.getImporteIGV());
			totalImpuestos.setMontoBase(totalesT.getValorOperacionesGravadas());
			totalesT.getSumatoriaImpuesto().add(totalImpuestos);

			if (MonedaType.USD.name().equals(encabezado.getMoneda())) {
				totalesT.setImporteTotal(encabezado.getImporteDolares());

			} else if (MonedaType.EUR.name().equals(encabezado.getMoneda())) {
				totalesT.setImporteTotal(encabezado.getImporteDolares());
			} else {
				totalesT.setImporteTotal(encabezado.getImporteSoles());
			}

			cpe.setTotales(totalesT);

			ObservacionType ot;
			for (ReferenciaBEAN ref : referencias) {
				try {
					if (Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
							.equals(Constantes.MICHELL_TIPO_DOCUMENTO.NOTA_CREDITO)
							|| Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
									.equals(Constantes.MICHELL_TIPO_DOCUMENTO.NOTA_DEBITO)) {

						logger.info("ES una Nota");
						final ReferenciaComercialType referenciaComercial = new ReferenciaComercialType();
						referenciaComercial.setTipoComprobante(Constantes.mapTipoDocumentoComercialMichel
								.get(Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(ref.getTipoDocumento())));
						referenciaComercial.setSerie(ref.getSerie());
						referenciaComercial.setNumero(ref.getCorrelativo().longValue());
						if (ref.getFechaReferencia() != null)
							referenciaComercial.setFecha(toXMLDate(ref.getFechaReferencia()));
						cpe.getReferenciaComercial().add(referenciaComercial);

						ot = new ObservacionType();
						ot.setNombre("MODIFIED_DATE");
						ot.setContenido(ref.getFechaReferencia().toString());
						cpe.getObservacionAdicional().add(ot);

					} else
						logger.info("No es una Nota");

				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
			}

			if (descuentoOtros.compareTo(BigDecimal.ZERO) > 0) {
				ot = new ObservacionType();
				ot.setNombre("DESCUENTO_OTRO");
				ot.setContenido(descuentoOtros.toString());
				cpe.getObservacionAdicional().add(ot);
			}

			// SE DEFINE FORMATO PARA SOL ALPACA
			if (encabezado.getOrigen() != null && dao.isSolAlpaca(encabezado.getOrigen(), 66) // 66
																								// es
																								// SOL
																								// Alpaca
																								// .esto
																								// debe
																								// ir
																								// en
																								// otro
																								// lado.
					&& (condiciones.getTipoOperacion() == null
							|| !condiciones.getTipoOperacion().equals(TipoOperacionType.ExportacionBienes))) {
				ot = new ObservacionType();
				ot.setNombre("DCL_PLANTILLA");
				ot.setContenido(cpe.getEmisor().getRuc() + "CPE"
						+ Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(cpe.getTipoComprobante().toString()).getCode() + "V"
						+ "2.0");
				cpe.getObservacionAdicional().add(ot);

				if (glosaCambio != null) {
					ot = new ObservacionType();
					ot.setNombre("GLOSA_CAMBIO");
					ot.setContenido(glosaCambio);
					cpe.getObservacionAdicional().add(ot);
				}

				ot = new ObservacionType();
				ot.setNombre("TOTAL_PAGAR");
				ot.setContenido(cpe.getTotales().getImporteTotal().subtract(descuentoOtros).toPlainString());
				cpe.getObservacionAdicional().add(ot);

				if (cpe.getTotales().getImporteTotal().subtract(descuentoOtros).compareTo(BigDecimal.valueOf(0)) < 1) {
					ot = new ObservacionType();
					ot.setNombre("TRANSFERENCIA_GRATUITA");
					ot.setContenido("Transferencia Gratuita de un Bien y/o Servicio Prestado Gratuitamente.");
					cpe.getObservacionAdicional().add(ot);

				}
			}

			if (detalles.get(0).getGlosaPago() != null) {
				ot = new ObservacionType();
				ot.setNombre("CONDICIONES_PAGO");
				ot.setContenido(detalles.get(0).getGlosaPago().trim());
				cpe.getObservacionAdicional().add(ot);
			}

			if (vendedor != null && vendedor.getNombre() != null) {
				ot = new ObservacionType();
				ot.setNombre("CAJERO");
				ot.setContenido(vendedor.getNombre());
				cpe.getObservacionAdicional().add(ot);
			}

			if (condiciones.getTipoOperacion() != null
					&& condiciones.getTipoOperacion().equals(TipoOperacionType.ExportacionBienes)) {
				if (incoterm != null) {
					ot = new ObservacionType();
					ot.setNombre("INCOTERM");
					ot.setContenido(incoterm);
					cpe.getObservacionAdicional().add(ot);
				}

				if (observacionAduana != null) {
					ot = new ObservacionType();
					ot.setNombre("OBSERVACIONES_ADUANA");
					ot.setContenido(observacionAduana);
					cpe.getObservacionAdicional().add(ot);
				}

				if (encabezado.getTipoMuestra() != null && encabezado.getTipoMuestra().intValue() == 1) { // cuando
																											// el
																											// campo
																											// TIPO_MUESTRA
																											// sea
																											// 1
																											// que
																											// equivale
																											// a
																											// SIN
																											// VALOR
																											// COMERCIAL.
					ot = new ObservacionType();
					ot.setNombre("TIPO_MUESTRA");
					ot.setContenido(
							"MUESTRAS SIN VALOR COMERCIAL PARA LA PROMOCIÓN DE NUESTRAS EXPORTACIONES NO TRADICIONALES.");
					cpe.getObservacionAdicional().add(ot);

				}

				// BULTOS_ADUANA
				if (detalles.get(0).getAduanaBultos() != null) {
					ot = new ObservacionType();
					ot.setNombre("BULTOS_ADUANA");
					ot.setContenido(detalles.get(0).getAduanaBultos());
					cpe.getObservacionAdicional().add(ot);

				}
			} else { // Factura Nacional

				// spot
				if (detalles.get(0).getAduanaBultos() != null) {
					ot = new ObservacionType();
					ot.setNombre("OPERACION_SPOT");
					ot.setContenido(detalles.get(0).getAduanaBultos());
					cpe.getObservacionAdicional().add(ot);
				}

				if (detalles.get(0).getCuentaCorrienteSPOT() != null) {
					ot = new ObservacionType();
					ot.setNombre("3001");
					ot.setContenido(detalles.get(0).getCuentaCorrienteSPOT());
					cpe.getObservacionAdicional().add(ot);
				}
			}

			if (sucursal != null) {
				ot = new ObservacionType();
				ot.setNombre("CODIGO_ESTABLECIMIENTO");
				ot.setContenido(sucursal.getCodigoSunat() != null ? sucursal.getCodigoSunat()
						: encabezado.getOrigen().toString());
				cpe.getObservacionAdicional().add(ot);

				ot = new ObservacionType();
				ot.setNombre("TIENDA");
				ot.setContenido(sucursal.getDescripcion());
				cpe.getObservacionAdicional().add(ot);

				if (sucursal.getDireccion() != null) {
					ot = new ObservacionType();
					ot.setNombre("DIRECCION_TIENDA");
					ot.setContenido(sucursal.getDireccion());
					cpe.getObservacionAdicional().add(ot);
				}
			}

			ot = new ObservacionType();
			ot.setNombre("TOTAL_CANTIDAD");
			ot.setContenido(totalCantidad.toString());
			cpe.getObservacionAdicional().add(ot);

			ot = new ObservacionType();
			ot.setNombre("TOTAL_UNIDADES");
			ot.setContenido(totalUnidades.toString());
			cpe.getObservacionAdicional().add(ot);

			ot = new ObservacionType();
			ot.setNombre("TASA_IGV");
			ot.setContenido(encabezado.getTasaIGV().toString() + "%");
			cpe.getObservacionAdicional().add(ot);

			ot = new ObservacionType();
			ot.setNombre("OBSERVACIONES");
			ot.setContenido(encabezado.getObservaciones());
			if (ot.getContenido() != null) {
				cpe.getObservacionAdicional().add(ot);
			}

			if (condiciones.getTipoOperacion() != null
					&& condiciones.getTipoOperacion().equals(TipoOperacionType.ExportacionBienes)) {
				ot = new ObservacionType();
				ot.setNombre("DCL_PLANTILLA");
				ot.setContenido(cpe.getEmisor().getRuc() + "CPE"
						+ Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(cpe.getTipoComprobante().toString()).getCode() + "V"
						+ "3.0");

				cpe.getObservacionAdicional().add(ot);
			}

			if (referencias.size() > 0 && Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(serie.getTipoDocumento())
					.equals(Constantes.MICHELL_TIPO_DOCUMENTO.FACTURA)) {
				ot = new ObservacionType();
				ot.setNombre("DOCUMENTO_REFERENCIA");
				String str = Constantes.MICHELL_TIPO_DOCUMENTO.fromValue(referencias.get(0).getTipoDocumento()).name();
				str += " " + referencias.get(0).getSerie();
				str += "-" + referencias.get(0).getCorrelativo().toString();
				ot.setContenido(str);
				cpe.getObservacionAdicional().add(ot);

			} else
				logger.info("no tiene referecia factura");

			Comprobante comprobante = of.createComprobante();
			comprobante.setCpe(cpe);

			// validaciones
			if (comprobante.getCpe().getSerie().startsWith("B")) {

				if (encabezado.getImporteSoles().compareTo(BigDecimal.valueOf(700)) == 1) {

					if (comprobante.getCpe().getAdquiriente().getNombre() == null
							|| comprobante.getCpe().getAdquiriente().getNombre().length() < 3) {
						throw new Exception(
								"Boleta debe ser Nomitava porque su Importe Total supera los S/ 700.00. Falta NOMBRE Adquiriente.");
					}
					if (comprobante.getCpe().getAdquiriente().getTipoIdentificacion() == null || comprobante.getCpe()
							.getAdquiriente().getTipoIdentificacion().equals(TipoIdentidadType.Ninguno)) {
						throw new Exception(
								"Boleta debe ser Nomitava porque su Importe Total supera los S/ 700.00. Falta Tipo Identificacion de Adquiriente.");
					}
					if (comprobante.getCpe().getAdquiriente().getNumeroIdentificacion() == null
							|| comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().length() < 3) {
						throw new Exception(
								"Boleta debe ser Nomitava porque su Importe Total supera los S/ 700.00. Falta Numero Identificacion de Adquiriente.");
					}
					if (comprobante.getCpe().getAdquiriente().getTipoIdentificacion().equals(TipoIdentidadType.DNI)
							&& comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().length() != 8
							&& comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().equals("00000000")
							&& comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().equals("11111111")) {
						throw new Exception("El dato ingresado como Numero de DNI es incorrecto.");
					}
				}
				if (comprobante.getCpe().getAdquiriente().getTipoIdentificacion().equals(TipoIdentidadType.DNI)
						&& comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().length() != 8) {
					throw new Exception("El dato ingresado como Numero de DNI es incorrecto: "
							+ comprobante.getCpe().getAdquiriente().getNumeroIdentificacion());
				}
				if (comprobante.getCpe().getAdquiriente().getTipoIdentificacion()
						.equals(TipoIdentidadType.CarnetExtranjeria)
						&& comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().length() < 6) {
					throw new Exception(
							"El dato ingresado como Numero de CARNET DE EXTRANJERÍA es incorrecto, debe ser mayor a 6 caracteres.");
				}
				if (comprobante.getCpe().getAdquiriente().getTipoIdentificacion().equals(TipoIdentidadType.Pasaporte)
						&& comprobante.getCpe().getAdquiriente().getNumeroIdentificacion().length() < 6) {
					throw new Exception(
							"El dato ingresado como Numero de PASAPORTE es incorrecto, debe ser mayor a 6 caracteres.");
				}

			}

			ByteArrayOutputStream out2 = new ByteArrayOutputStream();
			JAXB.marshal(comprobante, out2);
			out2.flush();
			out2.close();

			// TODO test
//			System.out.println(out2.toString());
//			Thread.sleep(100_000);

			// Emitir documento
			EmisionService_Service service = new EmisionService_Service(Constantes.wsdlLocation);
			EmisionService port = service.getEmisionServicePort();

			Declarar parameters = new Declarar();
			Calendar fecha = Calendar.getInstance();
			obj = new Formatter();
			int año = fecha.get(Calendar.YEAR);
			SimpleDateFormat format = new SimpleDateFormat("MM");
			XMLGregorianCalendar mesEmi = cpe.getCondiciones().getFechaEmision();
			GregorianCalendar gc = mesEmi.toGregorianCalendar();
			Date dt = gc.getTime();
			String mes = format.format(dt);

			if (mes.length() < 2)
				mes = 0 + mes;

			final Path pathDir = Paths.get("/nas198", "dir_facturas" + "/" + año + "/" + mes);

			if (Files.notExists(pathDir))
				Files.createDirectories(pathDir);

			String _tipoDocumento = String.format("%02d",
					Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(cpe.getTipoComprobante().toString()).getCode());

			parameters.setDocumento(out2.toString());
			parameters.setTipoDocumento(_tipoDocumento);
			parameters.setFormato(FormatoDeclararType.XM_LV_1_1);
			parameters.setRucEmisor(organizacion.getRuc());

			final DeclararResponse response = port.declarar(parameters);

			if (response.getReturn().getRespuesta().getEstado() == 2) {
				bean.setHash(response.getReturn().getHashComprobante());

			} else if (response.getReturn().getRespuesta().getEstado() == 1
					&& response.getReturn().getRespuesta().getDescripcion().contains("El comprobante ya existe")) {
				logger.info(response.getReturn().getRespuesta().getDescripcion());
				bean.setHash("VACIO");

			} else if (response.getReturn().getRespuesta().getEstado() != 2) {
				throw new Exception(
						"Error con transaccion[" + response.getReturn().getRespuesta().getDescripcion() + "].");
			}

			Obtener parametersObtener = new Obtener();
			parametersObtener.setRucEmisor(organizacion.getRuc());
			parametersObtener.setTipoDocumento(_tipoDocumento);
			parametersObtener.setSerie(cpe.getSerie());
			parametersObtener.setCorrelativo(Integer.toString(cpe.getCorrelativo()));
			parametersObtener.setCantidad(1);
			parametersObtener.setFormato(FormatoObtenerType.XML);

			ObtenerResponse responseObtiene = port.obtener(parametersObtener);

			logger.info("responseObtiene: " + responseObtiene.getReturn().getRespuesta().getDescripcion());
			bean.setXml(responseObtiene.getReturn().getXML());
			Path rutaXML = Files.write(
					pathDir.resolve(año + mes + "-" + bean.getNumeroInterno() + "-" + cpe.getSerie() + "-"
									+ obj.format("%08d", cpe.getCorrelativo()) + ".xml"),
					bean.getXml().getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.CREATE);

			bean.setRutaXML(pathDir.getParent().getParent().relativize(rutaXML).toString());

			String xmlSign = responseObtiene.getReturn().getXML();
			if (bean.getHash().equals("VACIO")) {
				xmlSign = xmlSign.substring(xmlSign.indexOf("DigestValue"));
				xmlSign = xmlSign.substring(xmlSign.indexOf(">") + 1);
				String hash = xmlSign.substring(0, xmlSign.indexOf("</"));

				bean.setHash(hash);
			}

			String timbre = cpe.getEmisor().getRuc() + "|"
					+ Constantes.SUNAT_TIPO_DOCUMENTO.valueOf(cpe.getTipoComprobante().toString()).getCode() + "|"
					+ cpe.getSerie() + "-" + cpe.getCorrelativo() + "|" + encabezado.getImporteIGV().toString() + "|"
					+ cpe.getTotales().getImporteTotal().toString() + "|"
					+ cpe.getCondiciones().getFechaEmision().toXMLFormat() + "|"
					+ cpe.getAdquiriente().getTipoIdentificacion().value()
					+ "|" + (cpe.getAdquiriente().getNumeroIdentificacion() != null
							? cpe.getAdquiriente().getNumeroIdentificacion() : "-")
					+ "|";

			timbre += bean.getHash();

			bean.setHash(timbre);
			logger.info("Timbre: " + bean.getHash());

			parametersObtener.setFormato(FormatoObtenerType.PDF);

			responseObtiene = port.obtener(parametersObtener);
			if (responseObtiene.getReturn().getPDF() != null) {

				bean.setPdf(responseObtiene.getReturn().getPDF());
				Path rutaPDF = Files.write(
						pathDir.resolve(año + mes + "-" + bean.getNumeroInterno() + "-" + cpe.getSerie() + "-"
										+ String.format("%08d", cpe.getCorrelativo()) + ".pdf"),
						bean.getPdf(), StandardOpenOption.CREATE);

				bean.setRutaPDF(pathDir.getParent().getParent().relativize(rutaPDF).toString());

			} else {
				logger.warn("Por que DOL no entrega PDF recien emitido?");
			}

			bean.setiPDF417(getPDF417(bean.getHash()));
			Path rutaPDF417 = Files.write(
					pathDir.resolve(año + mes + "-" + bean.getNumeroInterno() + "-" + cpe.getSerie() + "-"
									+ String.format("%08d", cpe.getCorrelativo()) + ".jpg"),
					getPDF417(bean.getHash()), StandardOpenOption.CREATE);
			
			bean.setRutaPDF417(pathDir.getParent().getParent().relativize(rutaPDF417).toString());

			dao.closeComprobante(bean, Constantes.MICHELL_ESTADO_COMPROBANTE.EMITIDO);
			con.commit();

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.warn(e, e);
			}

			logger.error("Exception con CorrelativoInterno[" + bean.getNumeroInterno().toString()
					+ "] y Numero Factura[" + bean.getCorrelativoComprobante().toString() + "]: " + e.getMessage(), e);

			try {
				dao.updateEstado(bean, Constantes.MICHELL_ESTADO_DOC_CERRADO.ABIERTO.getCode(),
						Constantes.MICHELL_ESTADO_COMPROBANTE.RECHAZO_DOL.getCode(), e.getMessage());
				con.commit();
			} catch (Exception e1) {
				logger.warn(e1.getMessage(), e1);
			}

		} finally {
			DataSourceFactory.desconectar(con);
			Constantes.TURNO.remove(bean.getOrganizacionID() + bean.getNumeroInterno().toString()
					+ bean.getCorrelativoComprobante().toString());
		}
	}

	private byte[] getPDF417(String hash) throws Exception {
		BarcodeQRCode PDF417 = new BarcodeQRCode(hash, 150, 150, null);

		java.awt.Image awtImage = PDF417.createAwtImage(Color.BLACK, Color.WHITE);
		BufferedImage bImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bImage.createGraphics();
		g.drawImage(awtImage, 0, 0, null);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bImage, "jpg", baos);
		baos.flush();
		baos.close();

		return baos.toByteArray();
	}

	public static XMLGregorianCalendar toXMLDate(Date date) throws DatatypeConfigurationException {
		GregorianCalendar calendar;

		calendar = new GregorianCalendar();
		calendar.setTime(date);

		return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(GregorianCalendar.YEAR),
				calendar.get(GregorianCalendar.MONTH) + 1, calendar.get(GregorianCalendar.DAY_OF_MONTH),
				DatatypeConstants.FIELD_UNDEFINED);
	}
}
