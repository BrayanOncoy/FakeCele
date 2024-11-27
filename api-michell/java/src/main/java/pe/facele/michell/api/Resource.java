package pe.facele.michell.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Resource {

	private static final String BUNDLE_NAME = "GMICHELL";
	private static final String BUNDLE_SQL = "SQLORACLE";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);

	private static final ResourceBundle Application = ResourceBundle.getBundle(BUNDLE_SQL, Locale.ENGLISH);

	
	/**
	 * Constructor vacío para las propiedades
	 */
	private Resource() {
	}

	/**
	 * @param key
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			// return '!' + key + '!';
			return null;
		}
	}

	public static boolean getBoolean(String key) {
		String s = getString(key);
		if (s != null)
			s = s.toLowerCase();
		return ("yes".equals(s) || "true".equals(s) || "1".equals(s));
	}

	public static int getInt(String key) {
		String s = getString(key);
		if (s != null) {
			s = s.toLowerCase();
			return Integer.parseInt(s);
		}
		return 0;
	}

	public static Integer getInteger(String key) {
		String s = getString(key);
		if (s != null) {
			s = s.toLowerCase();
			return new Integer(s);
		}
		return null;
	}

	public static String getID(Integer idOrganizacion) {
		return RESOURCE_BUNDLE.getString("EMISOR." + idOrganizacion + ".ID");
	}

	public static String getRUC(Integer idOrganizacion) {
		return RESOURCE_BUNDLE.getString("EMISOR." + idOrganizacion + ".RUC");
	}

	public static String getNOMBRE(Integer idOrganizacion) {
		return RESOURCE_BUNDLE.getString("EMISOR." + idOrganizacion + ".NOMBRE");
	}

	public static Integer getUBIGEO(Integer idOrganizacion) {
		return Integer.parseInt(RESOURCE_BUNDLE.getString("EMISOR." + idOrganizacion + ".UBIGEO"));
	}

	public static String getCODIGO_EXPORTACION_SUNAT(Integer idOrganizacion) {
		return RESOURCE_BUNDLE.getString("EMISOR." + idOrganizacion + ".CODIGO.EXPORTACION.SUNAT");
	}

	public static List<Integer> getOrganizacionIDs() {
		final List<Integer> result = new ArrayList<Integer>();
		String strIDs =  RESOURCE_BUNDLE.getString("ORGANIZACION.IDS");
		for (String strId : strIDs.split(",")) {
			if (strId.isEmpty())
				continue;
			result.add(Integer.parseInt(strId));
		}
		return result;
	}

	/**
	 * @return Returns the application.
	 */
	public static ResourceBundle getSQLResource() {
		return Application;
	}


}

