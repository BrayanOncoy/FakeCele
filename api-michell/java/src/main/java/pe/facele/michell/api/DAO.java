package pe.facele.michell.api;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import oracle.jdbc.OraclePreparedStatement;
import pe.facele.michell.api.Constantes.MICHELL_ESTADO_COMPROBANTE;
import pe.facele.michell.api.Constantes.MICHELL_ESTADO_DOC_CERRADO;
import pe.facele.michell.api.Constantes.MICHELL_ESTADO_RETENCION;
import pe.facele.michell.api.Constantes.MICHELL_ESTADO_RETENCION_REVERSION;
import pe.facele.michell.bean.AnticipoBEAN;
import pe.facele.michell.bean.CorrientistaBEAN;
import pe.facele.michell.bean.DetalleBEAN;
import pe.facele.michell.bean.EncabezadoBEAN;
import pe.facele.michell.bean.ItemBEAN;
import pe.facele.michell.bean.ItemRetailBEAN;
import pe.facele.michell.bean.OrganizacionBEAN;
import pe.facele.michell.bean.ReferenciaBEAN;
import pe.facele.michell.bean.RetencionBEAN;
import pe.facele.michell.bean.RetencionDetalleBEAN;
import pe.facele.michell.bean.SerieBEAN;
import pe.facele.michell.bean.TotalesBEAN;

public class DAO {
	private static final ResourceBundle sql = Resource.getSQLResource();
	private Connection con;
	Logger logger = Logger.getLogger(this.getClass());

	public DAO(Connection con) {
		this.con = con;
	}

	public EncabezadoBEAN getEncabezado(IdProceso bean) throws Exception {
		EncabezadoBEAN result = new EncabezadoBEAN();

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			/**
			 * ENGABEZADO.GET=SELECT E.FEC_EMISION, E.IDCORRENTISTA, E.RUC, E.NOMBRE,
			 * E.ORIGEN, E.CIUDAD_ORIGEN, E.DESTINO, E.MONEDA, E.FORMA_PAGO, E.IMP_FLETE,
			 * E.IMP_SEGURO, E.IMP_DCTO, E.PORC_IGV, E.IMP_IGV, E.IMP_NETO,
			 * E.IMP_PRECIO_BASE, E.IMP_SOLES, E.IMP_DOLARES, E.SALDO, E.SALDO_ANTICIPO,
			 * E.TIPO_CAMBIO, E.FEC_VCMTO, E.FEC_EMBARQUE, E.TRANSPORTISTA, E.TRANSPORTE,
			 * E.COMISIONISTA, E.DESCRIPCION, E.OBSERVACIONES, E.OBSERVA_ITEMS, E.USR_CREA
			 * FROM Z10.FACTURAS E WHERE E.NRO_INTERNO=? AND E.NRO_FACTURA=?
			 * 
			 */
			pst = con.prepareStatement(sql.getString("COMPROBANTE.ENCABEZADO.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());

			logger.debug("COMPROBANTE.ENCABEZADO.GET: " + sql.getString("COMPROBANTE.ENCABEZADO.GET"));
			rs = pst.executeQuery();

			if (rs.next()) {
				result.setFechaEmision(rs.getDate("FEC_EMISION"));
				result.setTipoFactura(rs.getInt("TIPO_FACTURA"));
				if (rs.getString("IDCORRENTISTA") != null)
					result.setIdCorrientista(rs.getLong("IDCORRENTISTA"));
				if (rs.getString("TIPO_DOC") != null)
					result.setIdentificacionTipo(rs.getString("TIPO_DOC"));
				if (rs.getString("NRO_DOC") != null)
					result.setIdentificacionNumero(rs.getString("NRO_DOC"));
				if (rs.getString("NOMBRE") != null)
					result.setNombre(rs.getString("NOMBRE"));
				result.setRuc(rs.getString("RUC"));
				if (rs.getString("ORIGEN") != null)
					result.setOrigen(rs.getInt("ORIGEN"));
				if (rs.getString("CIUDAD_ORIGEN") != null)
					result.setCiudadOrigen(rs.getInt("CIUDAD_ORIGEN"));
				if (rs.getString("DESTINO") != null)
					result.setDestino(rs.getInt("DESTINO"));
				result.setMoneda(rs.getString("MONEDA"));
				if (rs.getString("FORMA_PAGO") != null)
					result.setFormaPago(rs.getString("FORMA_PAGO"));

				if (rs.getString("IDCATALOGO_NCREDITO") != null)
					result.setTipoNotaCredito(rs.getInt("IDCATALOGO_NCREDITO"));

				if (rs.getString("IDCATALOGO_NDEBITO") != null)
					result.setTipoNotaDebido(rs.getInt("IDCATALOGO_NDEBITO"));

				if (rs.getString("MOTIVO_NOTAS_CONTA") != null)
					result.setSustento(rs.getString("MOTIVO_NOTAS_CONTA"));

				result.setImporteFlete(rs.getBigDecimal("IMP_FLETE"));
				result.setImporteSeguro(rs.getBigDecimal("IMP_SEGURO"));
				result.setImporteDescuento(rs.getBigDecimal("IMP_DCTO"));
				result.setTasaIGV(rs.getBigDecimal("PORC_IGV"));
				result.setImporteIGV(rs.getBigDecimal("IMP_IGV"));
				result.setImporteNeto(rs.getBigDecimal("IMP_NETO"));
				result.setImportePrecioBase(rs.getBigDecimal("IMP_PRECIO_BASE"));
				result.setImporteSoles(rs.getBigDecimal("IMP_SOLES"));
				result.setImporteDolares(rs.getBigDecimal("IMP_DOLARES"));
				result.setSaldo(rs.getBigDecimal("SALDO"));
				result.setSaldoAnticipo(rs.getBigDecimal("SALDO_ANTICIPO"));
				result.setTipoCambioEmision(rs.getBigDecimal("TIPO_CAMBIO"));
				if (rs.getString("FEC_EMBARQUE") != null)
					result.setFechaEmbarque(rs.getDate("FEC_EMBARQUE"));
				if (rs.getString("TRANSPORTISTA") != null)
					result.setTransportista(rs.getInt("TRANSPORTISTA"));
				if (rs.getString("TRANSPORTE") != null)
					result.setTransporte(rs.getString("TRANSPORTE"));
				if (rs.getString("COMISIONISTA") != null)
					result.setComisionista(rs.getLong("COMISIONISTA"));
				result.setDescripcion(rs.getString("DESCRIPCION"));
				result.setObservaciones(rs.getString("OBSERVACIONES"));
				result.setObservaItems(rs.getString("OBSERVA_ITEMS"));
				result.setUsuarioCreacion(rs.getString("USR_CREA"));
				if (rs.getString("IDVENDEDOR") != null)
					result.setIdVendedor(rs.getLong("IDVENDEDOR"));
				if (rs.getString("MOTIVO_REVERSION") != null)
					result.setSustento(rs.getString("MOTIVO_REVERSION"));
				if (rs.getString("TIPO_MUESTRA") != null)
					result.setTipoMuestra(rs.getInt("TIPO_MUESTRA"));

			} else
				throw new Exception("No existe un registro para este documento");

			return result;
		} catch (Exception e) {
			throw new Exception("Error: " + e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public SerieBEAN getSerie(IdProceso bean) throws Exception {
		SerieBEAN result;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("SERIE.GET"));
			logger.debug("SERIE.GET: " + sql.getString("SERIE.GET"));
			pst.setLong(1, bean.getNumeroInterno());

			rs = pst.executeQuery();

			if (rs.next()) {
				result = new SerieBEAN();
				result.setSerie(rs.getString("SERIE"));
				result.setTipoDocumento(rs.getInt("T_DOC"));
				result.setIdOrganizacion(rs.getInt("IDORGANIZACION"));
				result.setIdLugarEmision(rs.getInt("LUGAR_EMISION"));
				result.setIdCorrentistaDefault(rs.getInt("IDCORRENTISTA_DEFAULT"));
				result.setUnidadNegocio(rs.getInt("UNIDAD_NEGOCIO"));
				result.setNumeroSerieFabricacion(rs.getString("NRO_SERIE_FABRICACION"));
				result.setNumeroOrdenSUNAT(rs.getString("NRO_ORDEN_SUNAT"));

				return result;

			} else
				throw new Exception("SERIE no existe.");

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<ItemBEAN> getItems(IdProceso bean) throws Exception {
		List<ItemBEAN> result = new ArrayList<ItemBEAN>();
		ItemBEAN item;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("ITEMS.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("ITEMS.GET: " + sql.getString("ITEMS.GET"));
			rs = pst.executeQuery();

			while (rs.next()) {
				item = new ItemBEAN();
				item.setItem(rs.getInt("ITEM"));
				item.setCalidad(rs.getString("CALIDAD"));
				item.setTitulo(rs.getString("TITULO"));
				item.setLote(rs.getString("LOTE"));
				item.setMezcla(rs.getString("MEZCLA"));
				item.setColorMichell(rs.getString("COLOR_MICHELL"));
				item.setColorCliente(rs.getString("COLOR_CLIENTE"));
				item.setForma(rs.getString("FORMA"));
				if (rs.getString("PESO_PRESENT") != null)
					item.setPesoPresente(rs.getInt("PESO_PRESENT"));
				item.setCantidadPresente(rs.getBigDecimal("CANTIDAD_PRESENT"));
				item.setKilosBrutos(rs.getBigDecimal("KILOS_BRUTOS"));
				item.setKilosNetos(rs.getBigDecimal("KILOS_NETOS"));
				item.setPrecioUnitario(rs.getBigDecimal("PRECIO_UNITARIO"));
				item.setPrecioTotal(rs.getBigDecimal("PRECIO_TOTAL"));
				item.setValorUnitario(rs.getBigDecimal("VALOR_UNITARIO"));
				item.setValorTotal(rs.getBigDecimal("VALOR_TOTAL"));
				item.setTipoPartida(rs.getString("TIPO_PARTIDA"));
				item.setPartida(rs.getInt("PARTIDA"));
				if (rs.getString("TINADA") != null)
					item.setTinada(rs.getInt("TINADA"));
				item.setPrecioUnitarioDescuento(rs.getBigDecimal("PRECIO_UNITARIO_DSCTO"));
				item.setPrecioTotalDescuento(rs.getBigDecimal("PRECIO_TOTAL_DSCTO"));

				result.add(item);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public CorrientistaBEAN getCorrientista(Long idCorrientista) throws Exception {
		CorrientistaBEAN result;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("CORRENTISTA.GET"));
			pst.setLong(1, idCorrientista);
			logger.debug("CORRENTISTA.GET: " + sql.getString("CORRENTISTA.GET"));
			rs = pst.executeQuery();
			if (rs.next()) {
				result = new CorrientistaBEAN();
				result.setIdCorrentista(idCorrientista);
				result.setCiudadResidencia(rs.getInt("CIUDAD_RES"));
				result.setNombre(rs.getString("NOMBRE"));
				result.setPaisRecidencia(rs.getInt("PAIS_RES"));
				result.setTelefono(rs.getString("TELEFONO"));
				if (rs.getString("RUC") != null) {
					result.setIdentificacionTipo(Constantes.SUNAT_TIPO_IDENTIFICACION.RUC.getCode());
					result.setIdentificacionNumero(rs.getString("RUC"));
				} else {
					result.setIdentificacionTipo(Constantes.SUNAT_TIPO_IDENTIFICACION.NINGUNO.getCode());
					result.setIdentificacionNumero("-");
				}
				if (rs.getString("LE") != null && rs.getString("LE").matches("[0-9]*"))
					result.setLibretaElectoral(rs.getString("LE"));
				result.setDireccion(rs.getString("DIRECCION"));
				result.setEmail(rs.getString("E_MAIL"));
				result.setNombreComercial(rs.getString("NOMBRE_COMERCIAL"));

			} else
				throw new Exception("No hay registros para Correntista[" + idCorrientista.toString() + "]");

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public OrganizacionBEAN getOrganizacion(Integer idOrganizacion) throws Exception {
		OrganizacionBEAN result;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("ORGANIZACION.GET"));
			pst.setInt(1, idOrganizacion);
			logger.debug("ORGANIZACION.GET: " + sql.getString("ORGANIZACION.GET"));
			rs = pst.executeQuery();
			if (rs.next()) {
//				ORGANIZACION.GET=SELECT O., O., O., O., O., O., O., O., O. FROM Z10.ORGANIZACIONES O WHERE O.IDORGANIZACION=?

				result = new OrganizacionBEAN();
				result.setTipo(rs.getInt("TIPO"));
				result.setOrganizacionSuperior(rs.getInt("ORG_SUP"));
				result.setDescripcion(rs.getString("DESCRIPCION"));
				result.setDireccion(rs.getString("DIRECCION"));
				result.setRuc(rs.getString("RUC"));
				result.setTelefono(rs.getString("FONO1"));
				result.setEmail(rs.getString("E_MAIL"));
				result.setNombreVia(rs.getString("NOMBRE_VIA"));
				result.setNumeroVia(rs.getString("NUMERO_VIA"));
				result.setZona(rs.getString("ZONA"));
				result.setDistrito(rs.getString("DISTRITO"));
				result.setProvincia(rs.getString("PROVINCIA"));
				result.setCodigoSunat(rs.getString("CODIGO_SUNAT"));

			} else
				throw new Exception("No hay registros para Organizacion[" + idOrganizacion.toString() + "]");

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}

	}

	public List<ItemRetailBEAN> getItemsRetail(IdProceso bean) throws Exception {
		List<ItemRetailBEAN> result = new ArrayList<ItemRetailBEAN>();
		ItemRetailBEAN item;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("ITEMS_RETAIL.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("ITEMS_RETAIL.GET: " + sql.getString("ITEMS_RETAIL.GET"));
			rs = pst.executeQuery();

			while (rs.next()) {
				item = new ItemRetailBEAN();
				item.setItem(rs.getInt("ITEM"));
				item.setCodigo(rs.getInt("CODIGO"));
				item.setClase(rs.getInt("CLASE"));
				item.setDescripcion(rs.getString("DESCRIPCION"));
				item.setCaracteristica(rs.getString("CARACTERISTICA"));
				item.setMaterial(rs.getString("MATERIAL"));
				item.setComposicion(rs.getString("COMPOSICION"));
				item.setUnidad(rs.getString("IDUNIDAD"));
				item.setCantidad(rs.getBigDecimal("CANTIDAD"));
				item.setPorcentajeDescuento(rs.getBigDecimal("PORC_DSCTO"));
				item.setPrecioUnitario(rs.getBigDecimal("PRECIO_UNITARIO"));
				item.setPrecioDescuentoUnitario(rs.getBigDecimal("IMP_DSCTO"));
				item.setPrecioTotal(rs.getBigDecimal("PRECIO_TOTAL_NETO"));
				item.setValorUnitario(rs.getBigDecimal("VALOR_UNITARIO"));
				item.setValorDescuentoUnitario(rs.getBigDecimal("VALOR_DSCTO_UNITARIO"));
				item.setValorTotal(rs.getBigDecimal("VALOR_TOTAL_NETO"));
				item.setImporteIGVTotal(rs.getBigDecimal("IMP_IGV_TOTAL"));
				if (rs.getString("GR_SERIE") != null)
					item.setGrupoSerie(rs.getInt("GR_SERIE"));
				if (rs.getString("GR_NUMERO") != null)
					item.setGrupoNumero(rs.getInt("GR_NUMERO"));

				result.add(item);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public RetencionBEAN getEncabezadoRetencion(IdProceso bean) throws Exception {
		RetencionBEAN result = new RetencionBEAN();

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			/**
			 * SELECT R.FECHA_EMISION, R.ESTADO, R.NRO_MOV_CJA, R.IDCORRENTISTA FROM
			 * Z10.RETENCIONES WHERE R.NRO_INTERNO=? AND R.NRO_COMPROBANTE=?
			 * 
			 */
			pst = con.prepareStatement(sql.getString("RETENCIONES.ENCABEZADO.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());

			logger.debug("RETENCIONES.ENCABEZADO.GET: " + sql.getString("RETENCIONES.ENCABEZADO.GET"));
			rs = pst.executeQuery();

			if (rs.next()) {
				result.setFechaEmision(rs.getDate("FECHA_EMISION"));
				result.setEstado(rs.getInt("ESTADO"));
				if (rs.getBigDecimal("NRO_MOV_CJA") != null)
					result.setNumeroMovimientoCaja(rs.getBigDecimal("NRO_MOV_CJA").toBigInteger());
				result.setIdCorrientista(rs.getLong("IDCORRENTISTA"));
				result.setEmisionElectronica(rs.getInt("EMISION_ELECTRONICA"));
				result.setSustento(rs.getString("MOTIVO_REVERSION"));
				result.setNumeroCheque(rs.getString("NRO_CHEQUE"));
				if (rs.getString("FILE_CONTAB") != null && rs.getString("PERIODO") != null
						&& rs.getString("COMPROBANTE") != null)
					result.setAsientoContable(rs.getString("FILE_CONTAB") + "-" + rs.getString("PERIODO") + "-"
							+ rs.getString("COMPROBANTE"));
				result.setTasaCambio(rs.getBigDecimal("CAMBIO"));

			} else
				throw new Exception("Error lógico para consulta ");

			result.setCorrelativoInterno(bean.getNumeroInterno());
			result.setCorrelativoComprobante(bean.getCorrelativoComprobante());

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<RetencionDetalleBEAN> getDetalleRetenciones(IdProceso bean) throws Exception {
		List<RetencionDetalleBEAN> result = new ArrayList<RetencionDetalleBEAN>();
		RetencionDetalleBEAN item;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("RETENCIONES.REFERENCIAS.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("RETENCIONES.REFERENCIAS.GET: " + sql.getString("RETENCIONES.REFERENCIAS.GET"));
			rs = pst.executeQuery();

			while (rs.next()) {
				item = new RetencionDetalleBEAN();
				item.setTipoComprobante(rs.getInt("COD_CONTAB"));
				item.setSerie(rs.getString("SERIE"));
				item.setCorrelativo(rs.getLong("NRO_DOCUMENTO"));
				item.setFechaEmision(rs.getDate("FECHA_DOC"));
				item.setMoneda(rs.getString("CODIGO_ISO"));
				item.setImporteTotal(rs.getBigDecimal("IMPORTE_DOC"));
				item.setImportePagadoBrutoPEN(rs.getBigDecimal("MONTO_PAGO"));
				item.setImporteRetenidoPEN(rs.getBigDecimal("IMP_RETENIDO"));
				result.add(item);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public void insertPDF(RetencionBEAN bean) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("RETENCIONES.UPDATE"));
			logger.debug("RETENCIONES.UPDATE: " + sql.getString("RETENCIONES.UPDATE"));
			pst.setInt(1, bean.getEmisionElectronica());
			pst.setBytes(2, bean.getPdf());
			pst.setBytes(3, bean.getXml());
			pst.setLong(4, bean.getCorrelativoInterno());
			pst.setLong(5, bean.getCorrelativoComprobante());

			int rs = pst.executeUpdate();
			if (rs != 1)
				throw new Exception("Error logico porque no no se actualizo un registro. Registros modificados: " + rs);

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public int updateRetencionEstado(IdProceso bean, int estado_electronico) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("RETENCIONES.UPDATE.ESTADO"));
			logger.debug("RETENCIONES.UPDATE.ESTADO: " + sql.getString("RETENCIONES.UPDATE.ESTADO"));
			pst.setInt(1, estado_electronico);
			pst.setLong(2, bean.getNumeroInterno());
			pst.setLong(3, bean.getCorrelativoComprobante());

			int rs = pst.executeUpdate();

			if (rs != 1)
				throw new Exception("Error logico porque no no se actualizo un registro. Registros modificados: " + rs);

			return rs;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<RetencionBEAN> getRetencionesReversadas(IdProceso bean, int MICHELL_ESTADO_RETENCION, Date fechaEmision)
			throws Exception {
		List<RetencionBEAN> result = new ArrayList<RetencionBEAN>();
		RetencionBEAN retenciones;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setInt(2, MICHELL_ESTADO_RETENCION);
			pst.setDate(3, new java.sql.Date(fechaEmision.getTime()));
			logger.debug("REVERSIONES.RETENCIONES.GET: " + sql.getString("REVERSIONES.RETENCIONES.GET"));

			rs = pst.executeQuery();

			while (rs.next()) {
				retenciones = new RetencionBEAN();
				retenciones.setCorrelativoComprobante(rs.getLong("NRO_COMPROBANTE"));
				retenciones.setEstado(rs.getInt("ESTADO"));
				retenciones.setSustento(rs.getString("MOTIVO_REVERSION"));
				result.add(retenciones);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<DetalleBEAN> getDetalleComprobante(IdProceso bean) throws Exception {
		if (bean.getOrganizacionID() == 100 || bean.getOrganizacionID() == 106 || bean.getOrganizacionID() == 260
				|| bean.getOrganizacionID() == 240)
			return getDetalleComprobanteDinamico(bean);
		else
			throw new Exception("Error logico.. no se identifica Organizacion: " + bean.getOrganizacionID());

	}

	private List<DetalleBEAN> getDetalleComprobanteDinamico(IdProceso bean) throws Exception {
		List<DetalleBEAN> result = new ArrayList<DetalleBEAN>();
		DetalleBEAN detalle;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString(bean.getOrganizacionID() + ".COMPROBANTE.DETALLE.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("COMPROBANTES.DETALLE.GET: "
					+ sql.getString(bean.getOrganizacionID() + ".COMPROBANTE.DETALLE.GET"));

			rs = pst.executeQuery();
			while (rs.next()) {
				detalle = new DetalleBEAN();
				detalle.setUnidadMedida(rs.getString("UNIDAD"));
				detalle.setSubCantidad(rs.getBigDecimal("CANT_UNIDADES"));
				detalle.setCantidad(rs.getBigDecimal("CANTIDAD"));
				detalle.setCodigo(rs.getString("COD_PRODUCTO"));
				detalle.setDescripcion(rs.getString("DESC_COD_PRODUCTO"));
				detalle.setValorUnitario(rs.getBigDecimal("VALOR_UNITARIO_SUNAT"));
				detalle.setPrecioUnitario(rs.getBigDecimal("PRECIO_UNITARIO_SUNAT"));
				detalle.setIgvMonto(rs.getBigDecimal("IGVMONTO"));
				detalle.setIgvCodigo(rs.getInt("IGV_CODIGO"));
				detalle.setDescuento(rs.getBigDecimal("DESCUENTO_TOTAL_ITEM"));
				detalle.setValorItem(rs.getBigDecimal("VALOR_VENTA_ITEM"));
				detalle.setGlosaPago(rs.getString("TIPO_FACTURA"));
				detalle.setTipoIdentificacionAdquiriente(rs.getString("T_DOC_CLIENTE"));
				detalle.setNumeroIdentificacionAdquiriente(rs.getString("COD_CLIENTE"));
				detalle.setNombreAdquiriente(rs.getString("NOMBRE_CLIENTE"));
				detalle.setFlete(rs.getBigDecimal("FLETE"));
				detalle.setSeguro(rs.getBigDecimal("SEGURO"));
				detalle.setAduanaBultos(rs.getString("OBSERVACIONES2"));
				detalle.setDireccionAdquiriente(rs.getString("DIRECCION"));
				detalle.setCuentaCorrienteSPOT(rs.getString("CTACTE_SPOT"));
				detalle.setGastosFinancieros(rs.getBigDecimal("GTOS_FINANCIEROS"));
				detalle.setDescuentoGlobal(rs.getBigDecimal("IMP_DSCTO_GLOBAL"));
				if (rs.getString("FORMA_PAGO") != null)
					detalle.setFormaPago(rs.getString("FORMA_PAGO"));
				if (rs.getBigDecimal("MONTO_NETO_PAGO") != null)
					detalle.setMontoNetoPago(rs.getBigDecimal("MONTO_NETO_PAGO"));
				if (rs.getString("CUOTA_PAGO") != null)
					detalle.setNumeroCuota(rs.getString("CUOTA_PAGO"));
				if (rs.getString("MONTO_CUOTA_PAGO") != null)
					detalle.setMontoCuotaPago(rs.getBigDecimal("MONTO_CUOTA_PAGO"));
				if (rs.getString("FECHA_VCMTO_CUOTA_PAGO") != null)
					detalle.setFechaVencimientoCuota(rs.getDate("FECHA_VCMTO_CUOTA_PAGO"));
				result.add(detalle);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	@Deprecated
	public Integer getCodigoSunat(Integer tipoNotaCredito) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("SUNAT.CODIGO.GET"));
			pst.setInt(1, tipoNotaCredito);
			logger.debug("SUNAT.CODIGO.GET: " + sql.getString("SUNAT.CODIGO.GET"));

			rs = pst.executeQuery();
			if (rs.next())
				return rs.getInt("CODIGO_SUNAT");

			return null;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<ReferenciaBEAN> getReferencias(IdProceso bean) throws Exception {
		List<ReferenciaBEAN> result = new ArrayList<ReferenciaBEAN>();
		ReferenciaBEAN referencia;

		PreparedStatement pst = null;
		ResultSet rs = null;

		/**
		 * SELECT T_DOC_SUNAT, SERIE, CORRELATIVO FROM V_COMPROBANTES_RELACIONADOS WHERE
		 * NUMERO_INTERNO=? AND NUMERO_DOCUMENTO=?
		 */
		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.REFERENCIAS.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("COMPROBANTE.REFERENCIAS.GET: " + sql.getString("COMPROBANTE.REFERENCIAS.GET"));

			rs = pst.executeQuery();
			while (rs.next()) {
				referencia = new ReferenciaBEAN();
				referencia.setTipoDocumento(rs.getInt("T_DOC_SUNAT"));
				referencia.setSerie(rs.getString("SERIE"));
				referencia.setCorrelativo(rs.getInt("CORRELATIVO"));
				referencia.setFechaReferencia(rs.getDate("FECHA_DOCUMENTO_ORIGEN"));

				result.add(referencia);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public int closeComprobante(IdProceso bean, MICHELL_ESTADO_COMPROBANTE mICHELL_ESTADO_COMPROBANTE)
			throws Exception {
		OraclePreparedStatement pst = null;

		String rutaPDF = "bfilename('DIR_FACTURAS','" + bean.rutaPDF + "')";
		String rutaXML = "bfilename('DIR_FACTURAS','" + bean.rutaXML + "')";
		String rutaPDF417 = "bfilename('DIR_FACTURAS','" + bean.rutaPDF417 + "')";
		String sqlPDF = "UPDATE FACTURAS SET FILE_COMPROBANTE_1 = " + rutaPDF + " WHERE NRO_INTERNO = "
				+ bean.getNumeroInterno() + " AND NRO_FACTURA = " + bean.getCorrelativoComprobante();
		String sqlXML = "UPDATE FACTURAS SET XML_COMPROBANTE_1 = " + rutaXML + " WHERE NRO_INTERNO = "
				+ bean.getNumeroInterno() + " AND NRO_FACTURA = " + bean.getCorrelativoComprobante();
		String sqlPDF417 = "UPDATE FACTURAS SET PDF417_1 = " + rutaPDF417 + " WHERE NRO_INTERNO = "
				+ bean.getNumeroInterno() + " AND NRO_FACTURA = " + bean.getCorrelativoComprobante();

		OraclePreparedStatement ops = (OraclePreparedStatement) con.prepareStatement(sqlPDF);
		OraclePreparedStatement ops1 = (OraclePreparedStatement) con.prepareStatement(sqlXML);
		OraclePreparedStatement ops2 = (OraclePreparedStatement) con.prepareStatement(sqlPDF417);

		try {
			pst = (OraclePreparedStatement) con.prepareStatement(sql.getString("COMPROBANTE.UPDATE.CLOSE"));
			logger.debug("COMPROBANTE.UPDATE.CLOSE: " + sql.getString("COMPROBANTE.UPDATE.CLOSE"));
			pst.setInt(1, mICHELL_ESTADO_COMPROBANTE.getCode());
			pst.setNull(2, java.sql.Types.BINARY);
			pst.setNull(3, java.sql.Types.BINARY);
			pst.setString(4, bean.getHash());
			pst.setNull(5, java.sql.Types.BINARY);
			pst.setLong(6, bean.getNumeroInterno());
			pst.setLong(7, bean.getCorrelativoComprobante());

			ops.executeUpdate();
			ops1.executeUpdate();
			ops2.executeUpdate();

			int rs = pst.executeUpdate();

			if (rs != 1)
				throw new Exception("Error logico porque no no se actualizo un registro. Registros modificados: " + rs);

			return rs;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public int updateEstado(IdProceso bean, int MICHELL_ESTADO_DOC_CERRADO, int MICHELL_ESTADO_COMPROBANTE,
			String message) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.UPDATE.ESTADO"));
			logger.debug("COMPROBANTE.UPDATE.ESTADO: " + sql.getString("COMPROBANTE.UPDATE.ESTADO"));
			pst.setInt(1, MICHELL_ESTADO_DOC_CERRADO);
			pst.setInt(2, MICHELL_ESTADO_COMPROBANTE);
			pst.setString(3, message);
			pst.setLong(4, bean.getNumeroInterno());
			pst.setLong(5, bean.getCorrelativoComprobante());

			int rs = pst.executeUpdate();

			if (rs != 1)
				throw new Exception("Error logico porque no se actualizo un registro. Registros modificados: " + rs);

			return rs;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public boolean isSolAlpaca(Integer origen, int ORG_SUP) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.IS.SOLALPACA"));
			pst.setInt(1, origen);
			pst.setInt(2, ORG_SUP);
			logger.debug("COMPROBANTE.IS.SOLALPACA: " + sql.getString("COMPROBANTE.IS.SOLALPACA"));

			rs = pst.executeQuery();
			if (rs.next())
				return true;
			else
				return false;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public String getGlosaCambio(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.GLOSA.CAMBIO.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("COMPROBANTE.GLOSA.CAMBIO.GET: " + sql.getString("COMPROBANTE.GLOSA.CAMBIO.GET"));

			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			} else

				return null;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}

	}

	public List<IdProceso> getComprobantesBaja(Integer organizacionID, int codeEstado, int codeEmision_1,
			int codeEmision_2) throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("BAJA.COMPROBANTE.ENCABEZADO.CORRELATIVO"));
			pst.setInt(1, organizacionID);
			pst.setInt(2, codeEstado);
			pst.setInt(3, codeEmision_1);
			pst.setInt(4, codeEmision_2);
			logger.debug("BAJA.COMPROBANTE.ENCABEZADO.CORRELATIVO: "
					+ sql.getString("BAJA.COMPROBANTE.ENCABEZADO.CORRELATIVO"));

			rs = pst.executeQuery();
			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setNumeroInterno(rs.getLong("NRO_INTERNO"));
				id.setCorrelativoComprobante(rs.getLong("NRO_FACTURA"));
				id.setFechaEmision(rs.getDate("FEC_EMISION"));
				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public void updateComprobanteBaja(List<IdProceso> toUpdate, int codeEstado) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("BAJA.COMPROBANTE.ENCABEZADO.CORRELATIVO_UPDATE"));

			for (IdProceso comprobanteID : toUpdate) {
				pst.setInt(1, codeEstado);
				pst.setLong(2, comprobanteID.getNumeroInterno());
				pst.setLong(3, comprobanteID.getCorrelativoComprobante());

				pst.addBatch();
			}

			logger.debug("BAJA.COMPROBANTE.ENCABEZADO.CORRELATIVO_UPDATE: "
					+ sql.getString("BAJA.COMPROBANTE.ENCABEZADO.CORRELATIVO_UPDATE"));

			int[] rows = pst.executeBatch();
			for (int i = 0; i < rows.length; i++) {
				logger.debug("cantidad registros midificados: " + rows[i]);

			}

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<IdProceso> getComprobantesReversion(Integer organizacionId, int codeEstado, int codeEmision)
			throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO"));
			pst.setInt(1, organizacionId);
			pst.setInt(2, codeEstado);
			pst.setInt(3, codeEmision);
			logger.debug("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO: "
					+ sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO"));

			rs = pst.executeQuery();
			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setNumeroInterno(rs.getLong("NRO_INTERNO"));
				id.setCorrelativoComprobante(rs.getLong("NRO_COMPROBANTE"));
				id.setFechaEmision(rs.getDate("FECHA_EMISION"));
				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<Date> getComprobantesReversionFecha(Integer organizacionID, int codeEstado, int codeEmision)
			throws Exception {
		List<Date> result = new ArrayList<Date>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.FECHA"));
			pst.setInt(1, organizacionID);
			pst.setInt(2, codeEstado);
			pst.setInt(3, codeEmision);
			logger.debug("REVERSIONES.RETENCIONES.ENCABEZADO.FECHA: "
					+ sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.FECHA"));

			rs = pst.executeQuery();

			while (rs.next())
				result.add(rs.getDate(1));

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<IdProceso> getComprobantesReversion(Integer organizacionId, Date fecha, int codeEstado, int codeEmision)
			throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO_BYDATE"));
			pst.setInt(1, organizacionId);
			pst.setDate(2, new java.sql.Date(fecha.getTime()));
			pst.setInt(3, codeEstado);
			pst.setInt(4, codeEmision);
			logger.debug("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO_BYDATE: "
					+ sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO_BYDATE"));

			rs = pst.executeQuery();
			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setNumeroInterno(rs.getLong("NRO_INTERNO"));
				id.setCorrelativoComprobante(rs.getLong("NRO_COMPROBANTE"));
				id.setFechaEmision(rs.getDate("FECHA_EMISION"));
				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public void updateRetencionesReversadas(List<IdProceso> toUpdate, int codeEstado) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO_UPDATE"));

			for (IdProceso comprobanteID : toUpdate) {
				logger.debug("estado[" + codeEstado + "], numeroInterno[" + comprobanteID.getNumeroInterno().toString()
						+ "], numeroRetencion[" + comprobanteID.getCorrelativoComprobante().toString() + "]");
				pst.setInt(1, codeEstado);
				pst.setLong(2, comprobanteID.getNumeroInterno());
				pst.setLong(3, comprobanteID.getCorrelativoComprobante());

				pst.addBatch();
			}

			logger.debug("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO_UPDATE: "
					+ sql.getString("REVERSIONES.RETENCIONES.ENCABEZADO.CORRELATIVO_UPDATE"));

			int[] rows = pst.executeBatch();
			for (int i = 0; i < rows.length; i++) {
				logger.debug("columnas afectadas: " + rows[i]);

			}

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<IdProceso> getComprobantesEmitidos(Integer organizacionID) throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.ENCABEZADO.CORRELATIVO_ESTADO"));
			pst.setInt(1, organizacionID);
			logger.debug("COMPROBANTE.ENCABEZADO.CORRELATIVO_ESTADO: "
					+ sql.getString("COMPROBANTE.ENCABEZADO.CORRELATIVO_ESTADO"));

			rs = pst.executeQuery();

			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setOrganizacionID(organizacionID);
				id.setNumeroInterno(rs.getLong(1));
				id.setCorrelativoComprobante(rs.getLong(2));
				id.setFechaEmision(rs.getDate("FEC_EMISION"));

				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public String getPahtPDFComprobantes(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("BAJA.COMPROBANTE.GET.PDF"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("BAJA.COMPROBANTE.GET.PDF: " + sql.getString("BAJA.COMPROBANTE.GET.PDF"));

			rs = pst.executeQuery();
			if (rs.next())
				return rs.getString(1);

			throw new Exception("Error logico. No hay path PDF");
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}

	}

	@Deprecated
	public int changePDFComprobantes(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("BAJA.COMPROBANTE.UPDATE.PDF"));
			logger.debug("BAJA.COMPROBANTE.UPDATE.PDF: " + sql.getString("BAJA.COMPROBANTE.UPDATE.PDF"));
			pst.setBlob(1, new ByteArrayInputStream(bean.getPdf()));
			pst.setLong(2, bean.getNumeroInterno());
			pst.setLong(3, bean.getCorrelativoComprobante());

			int rs = pst.executeUpdate();

			if (rs != 1)
				throw new Exception("Error logico porque no no se actualizo un registro. Registros modificados: " + rs);

			return rs;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public byte[] getPDFRetenciones(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.GET.PDF"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("REVERSIONES.RETENCIONES.GET.PDF: " + sql.getString("REVERSIONES.RETENCIONES.GET.PDF"));

			rs = pst.executeQuery();
			if (rs.next())
				return rs.getBytes(1);

			throw new Exception("Error logico. No hay PDF");
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}

	}

	public int changePDFRetenciones(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("REVERSIONES.RETENCIONES.UPDATE.PDF"));
			logger.debug("REVERSIONES.RETENCIONES.UPDATE.PDF: " + sql.getString("REVERSIONES.RETENCIONES.UPDATE.PDF"));
			pst.setBlob(1, new ByteArrayInputStream(bean.getPdf()));
			pst.setLong(2, bean.getNumeroInterno());
			pst.setLong(3, bean.getCorrelativoComprobante());

			int rs = pst.executeUpdate();

			if (rs != 1)
				throw new Exception("Error logico porque no no se actualizo un registro. Registros modificados: " + rs);

			return rs;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<ReferenciaBEAN> getReferenciasNota(IdProceso bean) throws Exception {
		List<ReferenciaBEAN> result = new ArrayList<ReferenciaBEAN>();
		ReferenciaBEAN referencia;

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.REFERENCIAS.NOTA.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("COMPROBANTE.REFERENCIAS.NOTA.GET: " + sql.getString("COMPROBANTE.REFERENCIAS.NOTA.GET"));

			rs = pst.executeQuery();
			if (rs.next()) {
				referencia = new ReferenciaBEAN();
				referencia.setTipoDocumento(rs.getInt("TIPO_DOCUMENTO_REFERENCIA"));
				referencia.setSerie(rs.getString("SERIE_REFERENCIA"));
				referencia.setCorrelativo(rs.getInt("CORRELATIVO_REFERENCIA"));
				referencia.setCodigoSunat(rs.getInt("MOTIVO_SUNAT"));
				referencia.setSustento(rs.getString("SUSTENTO"));
				referencia.setFechaReferencia(rs.getDate("FECHA_EMISION_REFERENCIA"));

				result.add(referencia);
			} else
				throw new Exception("No hay documentos de referencia para Nota.");

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<IdProceso> getComprobantesEmitidos(Integer organizacionId, int MICHELL_ESTADO_COMPROBANTE)
			throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("SUCURSAL.COMPROBANTE.ENCABEZADO.CORRELATIVO"));
			pst.setInt(1, organizacionId);
			pst.setInt(2, MICHELL_ESTADO_COMPROBANTE);
			logger.debug("SUCURSAL.COMPROBANTE.ENCABEZADO.CORRELATIVO: "
					+ sql.getString("SUCURSAL.COMPROBANTE.ENCABEZADO.CORRELATIVO"));

			rs = pst.executeQuery();
			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setNumeroInterno(rs.getLong("NRO_INTERNO"));
				id.setCorrelativoComprobante(rs.getLong("NRO_FACTURA"));
				id.setFechaEmision(rs.getDate("FEC_EMISION"));
				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public byte[] getComprobante(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("SUCURSAL.COMPROBANTE.XML"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("SUCURSAL.COMPROBANTE.XML: " + sql.getString("SUCURSAL.COMPROBANTE.XML"));

			rs = pst.executeQuery();
			if (rs.next())
				return rs.getBytes(1);

			return null;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public String getIncoterm(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.INCOTERM"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("COMPROBANTE.INCOTERM: " + sql.getString("COMPROBANTE.INCOTERM"));

			rs = pst.executeQuery();
			if (rs.next())
				return rs.getString(1);

			return null;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public String getObservacionAduana(IdProceso bean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.ADUANA"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());
			logger.debug("COMPROBANTE.ADUANA: " + sql.getString("COMPROBANTE.ADUANA"));

			rs = pst.executeQuery();
			if (rs.next())
				return rs.getString(1);

			return null;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<SerieBEAN> getSeries(Integer idOrganizacion, Integer emisionElectronica) throws Exception {
		List<SerieBEAN> result = new ArrayList<SerieBEAN>();
		SerieBEAN bean;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement(sql.getString("SERIE.GET.LIST"));
			logger.debug("SERIE.GET: " + sql.getString("SERIE.GET.LIST"));
			pst.setInt(1, idOrganizacion);
			pst.setInt(2, emisionElectronica);

			rs = pst.executeQuery();

			while (rs.next()) {
				bean = new SerieBEAN();
				bean.setNumeroInterno(rs.getLong("NRO_INTERNO"));
				bean.setSerie(rs.getString("SERIE"));
				bean.setTipoDocumento(rs.getInt("T_DOC"));
				bean.setIdOrganizacion(rs.getInt("IDORGANIZACION"));
				bean.setIdLugarEmision(rs.getInt("LUGAR_EMISION"));
				bean.setIdCorrentistaDefault(rs.getInt("IDCORRENTISTA_DEFAULT"));
				bean.setUnidadNegocio(rs.getInt("UNIDAD_NEGOCIO"));
				bean.setNumeroSerieFabricacion(rs.getString("NRO_SERIE_FABRICACION"));
				bean.setNumeroOrdenSUNAT(rs.getString("NRO_ORDEN_SUNAT"));

				result.add(bean);
			}

			return result;
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<IdProceso> getComprobantesSinPDF(Long numeroInterno, MICHELL_ESTADO_COMPROBANTE emitido,
			MICHELL_ESTADO_DOC_CERRADO cerrado) throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.ENCABEZADO.CORRELATIVO_SIN_PDF"));
			pst.setLong(1, numeroInterno);
			pst.setInt(2, emitido.getCode());
			pst.setInt(3, cerrado.getCode());

			rs = pst.executeQuery();

			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setNumeroInterno(rs.getLong(1));
				id.setCorrelativoComprobante(rs.getLong(2));
				id.setFechaEmision(rs.getDate("FEC_EMISION"));
				id.setEstado(rs.getInt("ESTADO"));

				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<IdProceso> getRetencionesSinPDF(Long numeroInterno, MICHELL_ESTADO_RETENCION emitido,
			MICHELL_ESTADO_RETENCION_REVERSION cerrado) throws Exception {
		List<IdProceso> result = new ArrayList<IdProceso>();
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("RETENCIONES.ENCABEZADO.CORRELATIVO_SIN_PDF"));
			pst.setLong(1, numeroInterno);
			pst.setInt(2, emitido.getCode());
			pst.setInt(3, cerrado.getCode());

			rs = pst.executeQuery();

			IdProceso id;
			while (rs.next()) {
				id = new IdProceso();
				id.setNumeroInterno(rs.getLong(1));
				id.setCorrelativoComprobante(rs.getLong(2));
				id.setFechaEmision(rs.getDate("FECHA_EMISION"));
				id.setEstado(rs.getInt("ESTADO"));

				result.add(id);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public List<AnticipoBEAN> getAnticipos(IdProceso bean) throws Exception {

		List<AnticipoBEAN> res = new ArrayList<AnticipoBEAN>();
		AnticipoBEAN result = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql.getString("COMPROBANTE.ANTICIPOS.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());

			logger.debug("COMPROBANTE.ANTICIPOS.GET" + pst.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				result = new AnticipoBEAN();
				result.setRucEmisior(rs.getString("RUCEMISIONDOCUMENTO"));
				result.setTipoDocumentoMichell(rs.getInt("TIPODOCUMENTO"));
				result.setSerie(rs.getString("SERIEDOCUMENTO"));
				result.setCorrelativo(rs.getInt("CORRELATIVODOCUMENTO"));
				result.setImporteDocumento(rs.getBigDecimal("IMPORTEDOCUMENTO"));
				result.setImporteAcumulado(rs.getBigDecimal("TOTALANTICIPO"));
				res.add(result);
			}

			return res;

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}
	}

	public TotalesBEAN getTotales(IdProceso bean) throws Exception {

		TotalesBEAN result = null;

		ResultSet rs = null;
		PreparedStatement pst = null;
		try {

			pst = con.prepareStatement(sql.getString("COMPROBANTE.TOTALES.GET"));
			pst.setLong(1, bean.getNumeroInterno());
			pst.setLong(2, bean.getCorrelativoComprobante());

			logger.debug("COMPROBANTE.TOTALES.GET" + pst.toString());
			rs = pst.executeQuery();

			if (rs.next()) {
				result = new TotalesBEAN();
				result.setImporteTotal(rs.getBigDecimal("IMPORTE_TOTAL"));
				result.setValorGravado(rs.getBigDecimal("VALOR_OPERACION_GRAVADO"));
				result.setValorExonerado(rs.getBigDecimal("VALOR_OPERACION_EXONERADO"));
				result.setValorInafecto(rs.getBigDecimal("VALOR_OPERACION_INAFECTO"));
				result.setValorExportacion(rs.getBigDecimal("VALOR_OPERACION_EXPORTACION"));
				result.setValorGratuito(rs.getBigDecimal("VALOR_OPERACION_GRATUITO"));
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);

		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
			if (pst != null)
				try {
					pst.close();
				} catch (Exception e) {
					logger.error(e, e);
				}
		}

		return result;
	}
}
