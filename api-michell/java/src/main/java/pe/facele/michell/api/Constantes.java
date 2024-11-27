package pe.facele.michell.api;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.Preferences;
import org.apache.log4j.Logger;

import pe.facele.docele.schemas.v11.comprobante.TipoComprobanteType;
import pe.facele.docele.schemas.v11.comprobante.TipoReferenciaComercial;

public final  class Constantes {
	public static final ConcurrentHashMap<String, String> TURNO = new ConcurrentHashMap<String, String>();
	public static final ScheduledExecutorService SCHEDULE = Executors.newScheduledThreadPool(Resource.getInt("THREADS.SCHEDULE.CANTIDAD"));
	public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Resource.getInt("THREADS.EXECUTOR.CANTIDAD"));
	
	public static final Path HOME_PATH = Paths.get(System.getProperty("user.home", "null")).resolve("Facele").resolve("GMichell");
	public static final Map<MICHELL_TIPO_DOCUMENTO, TipoComprobanteType> mapTipoDocumentoMichell = new HashMap<MICHELL_TIPO_DOCUMENTO, TipoComprobanteType>();
	public static final Map<MICHELL_TIPO_DOCUMENTO, TipoReferenciaComercial>  mapTipoDocumentoComercialMichel = new HashMap<MICHELL_TIPO_DOCUMENTO, TipoReferenciaComercial>();
	
	public static final List<Integer> ORGANIZACON_IDS = Resource.getOrganizacionIDs();
	
	public static final String CODIGO_SUNAT = "CODIGO_SUNAT";
	
	public static URL wsdlLocation;
	public static String urldb;
	public static final BigDecimal TASA_IGV = new BigDecimal(Resource.getString("TASA.IGV"));
	public static Preferences DATA;

	static {
		try {
//			TODO INVERTIR
			DATA = Preferences.userNodeForPackage(Constantes.class);
//			DATA = Preferences.systemNodeForPackage(Constantes.class);
			
			Logger.getLogger(Constantes.class).info("Preferences: " + DATA.absolutePath());
			
			DATA.put("modalidad", "casa_matriz");
			DATA.put("wsdlLocation", Resource.getString("URL_DOL"));
			DATA.put("urldb", Resource.getString("BD.URL"));
			DATA.flush();
			DATA.sync();
			
			wsdlLocation = new URL(DATA.get("wsdlLocation", null));
			DATA.get("urldb", null);
			urldb = DATA.get("urldb", null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		mapTipoDocumentoMichell.put(MICHELL_TIPO_DOCUMENTO.BOLETA, TipoComprobanteType.BOLETA);
		mapTipoDocumentoMichell.put(MICHELL_TIPO_DOCUMENTO.FACTURA, TipoComprobanteType.FACTURA);
		mapTipoDocumentoMichell.put(MICHELL_TIPO_DOCUMENTO.NOTA_CREDITO, TipoComprobanteType.NOTA_CREDITO);
		mapTipoDocumentoMichell.put(MICHELL_TIPO_DOCUMENTO.NOTA_DEBITO, TipoComprobanteType.NOTA_DEBITO);
		
		mapTipoDocumentoComercialMichel.put(MICHELL_TIPO_DOCUMENTO.BOLETA, TipoReferenciaComercial.Boleta);
		mapTipoDocumentoComercialMichel.put(MICHELL_TIPO_DOCUMENTO.FACTURA, TipoReferenciaComercial.Factura);
	}

	public static enum FACELE_ESTADO {
		LOCAL(0), EN_DOCELE(1), RECHAZO_SUNAT(2), ACEPTADA_CON_REPAROS_SUNAT(3), ACEPTADO_SUNAT(4);
		private int value;

		public int getCode() {
			return this.value;
		}

		private FACELE_ESTADO(int value) {
			this.value = value;
		}

		public static FACELE_ESTADO fromValue(Integer v) {
			for (FACELE_ESTADO c : FACELE_ESTADO.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("FACELE_ESTADO: " + v);
		}
	};

	public static enum MICHELL_ESTADO_RETENCION {
		MANUAL(0), POREMITIR(1), EMITIDO(2), APROBADO(3), RECHAZADO(4);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_ESTADO_RETENCION(int value) {
			this.value = value;
		}

		public static MICHELL_ESTADO_RETENCION fromValue(Integer v) {
			for (MICHELL_ESTADO_RETENCION c : MICHELL_ESTADO_RETENCION.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_ESTADO_RETENCION: " + v);
		}
	};

	public static enum MICHELL_ESTADO_COMPROBANTE {
		MANUAL(0), POREMITIR(1), EMITIDO(2), RECHAZO_DOL(3), ARPOBADO(4), REPAROS(5), RECHAZO(6);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_ESTADO_COMPROBANTE(int value) {
			this.value = value;
		}

		public static MICHELL_ESTADO_COMPROBANTE fromValue(Integer v) {
			for (MICHELL_ESTADO_COMPROBANTE c : MICHELL_ESTADO_COMPROBANTE.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_ESTADO_COMPROBANTE: " + v);
		}
	};

	public static enum MICHELL_ESTADO_DOC_CERRADO {
		ABIERTO(0), CERRADO(1);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_ESTADO_DOC_CERRADO(int value) {
			this.value = value;
		}

		public static MICHELL_ESTADO_DOC_CERRADO fromValue(Integer v) {
			for (MICHELL_ESTADO_DOC_CERRADO c : MICHELL_ESTADO_DOC_CERRADO.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_ESTADO_DOC_CERRADO: " + v);
		}
	};

	public static enum MICHELL_ESTADO_RETENCION_REVERSION {
		ACTIVO(0), POREMITIR(2), EMITIDO(1);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_ESTADO_RETENCION_REVERSION(int value) {
			this.value = value;
		}

		public static MICHELL_ESTADO_RETENCION_REVERSION fromValue(Integer v) {
			for (MICHELL_ESTADO_RETENCION_REVERSION c : MICHELL_ESTADO_RETENCION_REVERSION.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_ESTADO_RETENCION_REVERSION: " + v);
		}
	};

	public static enum MICHELL_ESTADO_COMPROBANTE_BAJA {
		ACTIVO(0), POREMITIR(2), EMITIDO(1);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_ESTADO_COMPROBANTE_BAJA(int value) {
			this.value = value;
		}

		public static MICHELL_ESTADO_COMPROBANTE_BAJA fromValue(Integer v) {
			for (MICHELL_ESTADO_COMPROBANTE_BAJA c : MICHELL_ESTADO_COMPROBANTE_BAJA.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_ESTADO_COMPROBANTE_BAJA: " + v);
		}
	};

	public static enum MICHELL_TIPO_DOCUMENTO {
		RETENCION(163), FACTURA(153), NOTA_DEBITO(154), NOTA_CREDITO(155), BOLETA(158), GUIA_REMITENTE(9);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_TIPO_DOCUMENTO(int value) {
			this.value = value;
		}

		public static MICHELL_TIPO_DOCUMENTO fromValue(Integer v) {
			for (MICHELL_TIPO_DOCUMENTO c : MICHELL_TIPO_DOCUMENTO.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("TIPO_DOCUMENTO no soportado: " + v);
		}
	};

	public static enum MICHELL_TIPO_MONEDA {
		PEN(0), USD(1), DEM(2), ITL(3), CHF(4), GBP(5), FRF(6), EUR(7), ESP(8), JPY(9), CLP(11);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_TIPO_MONEDA(int value) {
			this.value = value;
		}

		public static MICHELL_TIPO_MONEDA fromValue(Integer v) {
			for (MICHELL_TIPO_MONEDA c : MICHELL_TIPO_MONEDA.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_TIPO_MONEDA no soportado: " + v);
		}
	};

	public static enum MICHELL_TIPO_FACTURA {
		CONTADO(0), CREDITO(1), ANTICIPO(2), EXPORTACION(1001);
		private int value;

		public int getCode() {
			return this.value;
		}

		private MICHELL_TIPO_FACTURA(int value) {
			this.value = value;
		}

		public static MICHELL_TIPO_FACTURA fromValue(Integer v) {
			for (MICHELL_TIPO_FACTURA c : MICHELL_TIPO_FACTURA.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_TIPO_FACTURA no soportado: " + v);
		}
	};

	public static enum MICHELL_UNIDAD_MEDIDA {
		KG("KGM"), UNI("NIU"), MT("MTR");

		private String value;

		public String getCode() {
			return this.value;
		}

		private MICHELL_UNIDAD_MEDIDA(String value) {
			this.value = value;
		}

		public static MICHELL_UNIDAD_MEDIDA fromValue(String v) {
			for (MICHELL_UNIDAD_MEDIDA c : MICHELL_UNIDAD_MEDIDA.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			throw new IllegalArgumentException("MICHELL_UNIDAD_MEDIDA no soportado: " + v);
		}
	};

	public static enum SUNAT_CODIGOS_AFECTACION_IGV {
		GRAVADO_OPERACION_ONEROSA(10), GRAVADO_RETIRO_POR_PREMIO(11), GRAVADO_RETIRO_POR_DONACION(12), GRAVADO_RETIRO(
				13), GRAVADO_RETIRO_POR_PUBLICIDAD(14), GRAVADO_BONIFICACION(
						15), GRAVADO_RETIRO_POR_ENTREGA_TRABAJADORES(16), EXONERADO_OPERACION_ONEROSA(
								20), EXONERADO_TRANSFERENCIA_GRATUITA(21), INAFECTO_OPERACION_ONEROSA(
										30), INAFECTO_RETIRO_BONIFICACION(31), INAFECTO_RETIRO(
												32), INAFECTO_RETIRO_MUESTRAS_MEDICAS(
														33), INAFECTO_RETIRO_CONVENIO_COLECTIVO(
																34), INAFECTO_RETIRO_POR_PREMIO(
																		35), INAFECTO_RETIRO_POR_PUBLICIDAD(
																				36), EXPORTACION(40);

		private int value;

		public int getCode() {
			return this.value;
		}

		private SUNAT_CODIGOS_AFECTACION_IGV(int value) {
			this.value = value;
		}

		public static SUNAT_CODIGOS_AFECTACION_IGV fromValue(Integer v) {
			for (SUNAT_CODIGOS_AFECTACION_IGV c : SUNAT_CODIGOS_AFECTACION_IGV.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("SUNAT_CODIGOS_AFECTACION_IGV no soportado: " + v);
		}
	};

	public static enum SUNAT_CODIGOS_NOTA_CREDITO {
		ANULACION_OPERACION(1), ANULACION_POR_ERROR_RUC(2), CORRECCION_DESCRIPCION(3), DESCUENTO_GLOBAL(
				4), DESCUENTO_ITEM(5), DEVOLUCION_GLOBAL(6), DEVOLUCION_POR_ITEM(7), BONIFICACION(8), DISMINUCION_VALOR(
						9), OTROS_CONCEPTOS(10), POR_TRASLADOS(11);

		private int value;

		public int getCode() {
			return this.value;
		}

		private SUNAT_CODIGOS_NOTA_CREDITO(int value) {
			this.value = value;
		}

		public static SUNAT_CODIGOS_NOTA_CREDITO fromValue(Integer v) {
			for (SUNAT_CODIGOS_NOTA_CREDITO c : SUNAT_CODIGOS_NOTA_CREDITO.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("SUNAT_CODIGOS_NOTA_CREDITO no soportado: " + v);
		}
	};

	public static enum SUNAT_CODIGOS_NOTA_DEBITO {
		INTERES_POR_MORA(1), AUMENTO_VALOR(2), PENALIDADES_OTROS_CONCEPTOS(3);

		private int value;

		public int getCode() {
			return this.value;
		}

		private SUNAT_CODIGOS_NOTA_DEBITO(int value) {
			this.value = value;
		}

		public static SUNAT_CODIGOS_NOTA_DEBITO fromValue(Integer v) {
			for (SUNAT_CODIGOS_NOTA_DEBITO c : SUNAT_CODIGOS_NOTA_DEBITO.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("SUNAT_CODIGOS_NOTA_DEBITO no soportado: " + v);
		}
	};

	public static enum SUNAT_TIPO_DOCUMENTO {
		FACTURA(1), BOLETA(3), NOTA_CREDITO(7), NOTA_DEBITO(8), GUIA_REMITENTE(9), TICKET(
				12), DOCUMENTOS_DE_LA_SUPERINTENDENCIA_BANCA(13), DOCUMENTOS_EMITIDOS_POR_AFP(18), RETENCION(
						20), GUIA_TRANSPORTISTA(31), PRECEPCION(40), COMPROBANTE_DE_PAGO_SEAE(56);

		private int value;

		public int getCode() {
			return this.value;
		}

		private SUNAT_TIPO_DOCUMENTO(int value) {
			this.value = value;
		}

		public static SUNAT_TIPO_DOCUMENTO fromValue(Integer v) {
			for (SUNAT_TIPO_DOCUMENTO c : SUNAT_TIPO_DOCUMENTO.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("SUNAT_TIPO_DOCUMENTO no soportado: " + v);
		}
	};

	public static enum SUNAT_TIPO_IDENTIFICACION {
		DOCUMENTO_NO_DOMICILIADO("0"), DNI("1"), CARNET_EXTRANJERIA("4"), RUC("6"), PASAPORTE("7"), CEDULA_DIPLOMATICA(
				"A"), NINGUNO("-");

		private String value;

		public String getCode() {
			return this.value;
		}

		private SUNAT_TIPO_IDENTIFICACION(String value) {
			this.value = value;
		}

		public static SUNAT_TIPO_IDENTIFICACION fromValue(String v) {
			for (SUNAT_TIPO_IDENTIFICACION c : SUNAT_TIPO_IDENTIFICACION.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			throw new IllegalArgumentException("SUNAT_TIPO_IDENTIFICACION no soportado: " + v);
		}
	};

	public static enum UNECE_UNIDAD_MEDIDA {
		NUMBER_INTERNATIONAL_UNITS("NIU"), KILOMETER("KTM"), KILOGRAM("KGM"), METRO("MTR"), TONNE("TNE");
		private String value;

		public String getCode() {
			return this.value;
		}

		private UNECE_UNIDAD_MEDIDA(String value) {
			this.value = value;
		}

		public static UNECE_UNIDAD_MEDIDA fromValue(String v) {
			for (UNECE_UNIDAD_MEDIDA c : UNECE_UNIDAD_MEDIDA.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			throw new IllegalArgumentException("UNECE_UNIDAD_MEDIDA no soportado: " + v);
		}
	};

	public static enum DOCELE_ESTADO_REVERSION {
		COMUNICACION_BAJA(0), REVERSION(1);
		private int value;

		public int getCode() {
			return this.value;
		}

		private DOCELE_ESTADO_REVERSION(int value) {
			this.value = value;
		}

		public static DOCELE_ESTADO_REVERSION fromValue(Integer v) {
			for (DOCELE_ESTADO_REVERSION c : DOCELE_ESTADO_REVERSION.values()) {
				if (c.value == v.intValue()) {
					return c;
				}
			}
			throw new IllegalArgumentException("DOCELE_ESTADO_REVERSION: " + v);
		}
	};
}
