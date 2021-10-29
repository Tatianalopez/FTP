package com.alianza.lib.utilitarios;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesSimbolos;

import ch.qos.logback.classic.pattern.Util;

public class HelperUtil {

	public static boolean isNullOrEmpty(final Object o) {
		if (o instanceof String) {
			return (o == null || ((String) o).isEmpty() || ConstantesSimbolos.NULL.equals((String) o));
		}
		return (o == null);
	}

	public static boolean valueOfBoolean(final Boolean o) {
		return isNullOrEmpty(o) ? Boolean.FALSE : o;
	}

	public static boolean esMayorAZero(Long o) {
		return o > 0;
	}

	public static boolean isNullOrEmpty(final Collection<?> c) {
		return c == null || c.isEmpty();
	}

	public static boolean isNullOrEmpty(final Map<?, ?> m) {
		return m == null || m.isEmpty();
	}

	public static String dateToString(Date date) {
		String stringDate = null;
		if (!isNullOrEmpty(date)) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				stringDate = dateFormat.format(date);
			} catch (Exception e) {
				Logs.LOG_ERRORES.error(Util.class.getName(), e);
			}
		}
		return stringDate;
	}

	public static String dateToStringFull(Date date) {
		String stringDate = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			stringDate = dateFormat.format(date);
		} catch (Exception e) {
			Logs.LOG_ERRORES.error(Util.class.getName(), e);
		}
		return stringDate;
	}

	public static String dateToString(Date date, String format) {
		String stringDate = null;
		if (!isNullOrEmpty(date)) {
			try {
				DateFormat dateFormat = new SimpleDateFormat(format);
				stringDate = dateFormat.format(date);
			} catch (Exception e) {
				Logs.LOG_ERRORES.error(Util.class.getName(), e);
			}
		}
		return stringDate;
	}

	public static Date stringToDate(String stringDate) {
		DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = null;
		if (!isNullOrEmpty(stringDate)) {
			try {
				date = readFormat.parse(stringDate);
			} catch (Exception e) {
				Logs.LOG_ERRORES.error(Util.class.getName(), e);
			}
		}
		return date;
	}

	public static boolean stringToBoolean(String stringValue) {
		stringValue = stringValue.toUpperCase();
		return stringValue.equals("SI");
	}

	public static String booleanToString(Boolean boolValue) {
		String valor;
		if (isNullOrEmpty(boolValue)) {
			valor = ConstantesSimbolos.VACIO;
		} else if (boolValue) {
			valor = "Si";
		} else {
			valor = "No";
		}
		return valor;
	}

	public static String validarString(String valor) {
		if (isNullOrEmpty(valor)) {
			valor = ConstantesSimbolos.VACIO;
		}
		if (valor.equalsIgnoreCase("true")) {
			valor = "Si";
		}
		if (valor.equalsIgnoreCase("false")) {
			valor = "No";
		}
		return valor.toUpperCase();
	}

	public static String stringToBooleanStringSIFI(String string) {
		if (isNullOrEmpty(string))
			return "N";
		if (string.equalsIgnoreCase("SI")) {
			return "S";
		} else {
			return "N";
		}
	}

	public static String stringSIFItoTexto(String string) {
		if (isNullOrEmpty(string))
			return "No";
		if (string.equalsIgnoreCase("S")) {
			return "Si";
		} else {
			return "No";
		}
	}

	public static String stringToSexoStringSIFI(String string) {
		if (isNullOrEmpty(string))
			return "F";
		if (string.equalsIgnoreCase("femenino")) {
			return "F";
		} else {
			return "M";
		}
	}

	public static String validarCurrencyAsString(String valor) {
		if (isNullOrEmpty(valor)) {
			valor = ConstantesSimbolos.CERO;
		}
		BigDecimal number;
		try {
			number = new BigDecimal(valor);
		} catch (Exception e) {
			number = new BigDecimal(ConstantesSimbolos.CERO);
		}
		return currencyFormat(number);
	}

	public static String currencyFormat(Number number) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return " " + formatter.format(number);
	}

	public static int stringToInteger(String toConvert) {
		try {
			return Integer.parseInt(toConvert);
		} catch (Exception e) {
			return 1;
		}
	}

	public static boolean equals(Object to, Object from) {
		try {
			return Objects.equals(to, from);
		} catch (Exception e) {
			return false;
		}
	}

	
	public static String capitalize(final String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
	}

	

	public static HttpServletRequest obtenerHttpSerlevtRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}



	/*
	 * Los Siguientes 5 métodos son una copia exacta de los utilitarios del proyecto
	 * wia-backend esto como resultado de la migración de la pantalla de
	 * previnculación terceros de wia a OFV INICIO
	 */

	/**
	 * Getter getQuitarTildes: Retorna el String sin tildes, Recorre el String y
	 * compara uno a uno sus caracteres y cuando encuentra un caracter especial lo
	 * remplaza por su equivalencia.
	 * 
	 * Adicionalmente elimina espacios, retornos y tabuladores que haya al principio
	 * o final del String
	 * 
	 * @param texto Se le pasa por parametro un String
	 * @return Retorna el String introducido sin tildes
	 */
	private static String getQuitarTildes(String texto) {
		String textoSinTildes = texto;

		// Cadena de caracteres que seran remplazados
		String especial = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
		// Cadena de caracteres que reemplaza a los especiales.
		String equivalencia = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";

		for (int i = 0; i < especial.length(); i++) {

			// Reemplazamos los caracteres especiales.
			textoSinTildes = textoSinTildes.replace(especial.charAt(i), equivalencia.charAt(i));
		}
		return textoSinTildes;
	}

	/**
	 * Getter getQuitarEspaciosInnecesarios: Retorna el String eliminandole
	 * espacios, retornos y tabuladores que haya al principio o final del String, al
	 * igual que espacios innecesarios entre el String
	 * 
	 * @param texto Se le pasa por parametro un String
	 * @return Retorna el String introducido sin espacios, retornos y tabuladores
	 *         que haya al principio o final del String, al igual que espacios
	 *         innecesarios entre el String
	 */
	public static String getQuitarEspaciosInnecesarios(String texto) {
		String textoSinEspaciosInnecesarios = texto;
		textoSinEspaciosInnecesarios = textoSinEspaciosInnecesarios.replaceAll(" +", " ");
		textoSinEspaciosInnecesarios = textoSinEspaciosInnecesarios.trim();
		return textoSinEspaciosInnecesarios;
	}

	/**
	 * Getter getQuitarTabulaciones: Retorna el String eliminandole las tabulaciones
	 * 
	 * @param texto Se le pasa por parametro un String
	 * @return Retorna el String introducido sin tabulaciones
	 */
	public static String getQuitarTabulaciones(String texto) {
		String textoSinTabulaciones = texto;
		textoSinTabulaciones = textoSinTabulaciones.replaceAll("	+", " ");
		textoSinTabulaciones = textoSinTabulaciones.trim();
		return textoSinTabulaciones;
	}

	/**
	 * Getter getConvertirEnMayusculas: Retorna el String en mayusculas
	 * 
	 * @param texto Se le pasa por parametro un String
	 * @return Retorna el String introducido convertido en mayusculas
	 */
	private static String getConvertirEnMayusculas(String texto) {
		String textoEnMayuscula = texto.toUpperCase();
		return textoEnMayuscula;
	}

	/**
	 * Getter getConvertirTexto: Retorna el String sin tildes, lo convierte en
	 * mayusculas, y le elimina los espacios, retornos y tabuladores que hayan al
	 * principio o final del String, al igual que los que esten mas de una vez.
	 * 
	 * @param texto Se le pasa por parametro un String
	 * @return Retorna el String sin tildes, lo convierte en mayusculas, y le
	 *         elimina los espacios, retornos y tabuladores que hayan al principio o
	 *         final del String
	 */
	public static String getConvertirTexto(String texto) {
		String textoFinal = texto;
		textoFinal = getQuitarTildes(textoFinal);
		textoFinal = getQuitarTabulaciones(textoFinal);
		textoFinal = getQuitarEspaciosInnecesarios(textoFinal);
		textoFinal = getConvertirEnMayusculas(textoFinal);
		return textoFinal;
	}
	
	/**
	 * Chequea si str puede convertirse a un valor numérico.
	 * 
	 * @param strNum
	 * @return
	 */
	public static boolean isNumeric(String strNum) {
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException | NullPointerException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Convierte un parámetro para trabajar con Like Clause
	 */
	public static String getConvertirLikeClause(String parametro) {
		return "%" + parametro + "%";
	}

	/**
	 * Valida si el texto es EMail
	 * 
	 * @param email, Direccion Electronica a validar
	 * 
	 * @return Valor booleano que indica si es una direccion electronica valida
	 * 
	 */

	public static boolean isEmail(String email) {
		try {
			if (email != null && email.length() > 5) {
				Pattern p = Pattern.compile("^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])[2]?\\.)+[a-z-A-Z-0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])[2]?$");
				Matcher m = p.matcher(email);
				return m.find();
			}
		} catch (Exception e) {
			Logs.error("Error validando direccion electronica " + email, e.getMessage());
		}
		return false;
	}
}
