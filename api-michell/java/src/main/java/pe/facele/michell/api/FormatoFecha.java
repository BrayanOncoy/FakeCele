package pe.facele.michell.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatoFecha {
	private static final ThreadLocal<DateFormat> dateFecha = new ThreadLocal<DateFormat>(){
		@Override
		protected DateFormat initialValue() {
		return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	public static Date convertFecha(String source) throws ParseException{
		Date d = dateFecha.get().parse(source);
		return d;
	}
	
	public static String convertFecha(Date source) throws ParseException{
		String d = dateFecha.get().format(source);
		return d;
	}

	private static final ThreadLocal<DateFormat> datePeriodo = new ThreadLocal<DateFormat>(){
		@Override
		protected DateFormat initialValue() {
		return new SimpleDateFormat("yyyy-MM");
		}
	};

	public static Date convertPeriodo(String source) throws ParseException{
		Date d = datePeriodo.get().parse(source);
		return d;
	}
	
	public static String convertPeriodo(Date source) throws ParseException{
		String d = datePeriodo.get().format(source);
		return d;
	}

	private static final ThreadLocal<DateFormat> timeStamp = new ThreadLocal<DateFormat>(){
		@Override
		protected DateFormat initialValue() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}
	};

	public static Date convertTimeStamp(String source) throws ParseException{
		Date d = timeStamp.get().parse(source);
		return d;
	}
	
	public static String convertTimeStamp(Date source) throws ParseException{
		String d = timeStamp.get().format(source);
		return d;
	}
}
