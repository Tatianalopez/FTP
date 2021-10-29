package com.alianza.lib.utilitarios;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.alianza.lib.excepciones.CustomRuntimeException;

public class PropertiesUtil {

	private static PropertiesUtil miPropertiesLoader = null;

	public static PropertiesUtil getPropertiesloader() {
		if (miPropertiesLoader == null) {
			miPropertiesLoader = new PropertiesUtil();
		}
		return miPropertiesLoader;
	}

	public Properties load(String fileName) {
		Properties prop = new Properties();
		try (InputStream im = findFile(fileName)) {
			prop.load(im);
			return prop;
		} catch (IOException ignore) {
			Logs.error("La carga de propiedades ha fallado >> ", ignore);
			throw new CustomRuntimeException("No ha sido posible cargar el archivo de propiedades ", fileName);
		}
	}

	public Properties load(String directoryName, String fileName) {
		Properties prop = new Properties();
		try (InputStream im = findFile(directoryName, fileName)) {
			prop.load(im);
			return prop;
		} catch (IOException ignore) {
			Logs.error("La carga de propiedades ha fallado >> ", ignore);
			throw new CustomRuntimeException("Ha fallado la cargar el archivo de propiedades desde ", fileName);
		}
	}

	private InputStream findFile(String fileName) throws FileNotFoundException {
		InputStream im = findInWorkingDirectory(fileName);
		if (im == null) {
			im = findInClasspath(fileName);
			if (im == null) {
				im = findInSourceDirectory(fileName);
				if (im == null) {
					throw new FileNotFoundException(String.format("Archivo %s no encontrado!", fileName));
				}
			}
		}
		return im;
	}

	private InputStream findFile(String directoryName, String fileName) throws FileNotFoundException {
		InputStream im = findInPrivateDirectory(directoryName, fileName);
		if (im == null)
			throw new FileNotFoundException(String.format("Archivo %s no encontrado!", fileName));
		return im;
	}

	private InputStream findInSourceDirectory(String fileName) {
		try {
			return new FileInputStream("src/main/resources/" + fileName);
		} catch (FileNotFoundException e) {
			Logs.error("findInSourceDirectory ha fallado >> ", e);
			throw new CustomRuntimeException("Ha fallado la cargar el archivo de propiedades desde ", fileName);
		}
	}

	private InputStream findInPrivateDirectory(String directoryName, String fileName) {
		try {
			return new FileInputStream(directoryName + fileName);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	private InputStream findInClasspath(String fileName) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
	}

	private InputStream findInWorkingDirectory(String fileName) {
		try {
			return new FileInputStream(System.getProperty("user.dir") + fileName);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * Método encargado de obtener un valor a partir de la llave correspondiente
	 * dentro de un Archivo de propiedades.
	 * 
	 * @param llave Llave dentro del archivo .properties
	 * @return String Valor
	 */
	public static String obtenerPropiedad(String llave) {
		try {
			Properties prop1 = PropertiesUtil.getPropertiesloader().load(ConstantesCoreUtil.PATH_PROPERTIES);
			Properties prop2 = PropertiesUtil.getPropertiesloader().load(
					prop1.getProperty(ConstantesCoreUtil.KEY_PATH_PROPERTIES_OFV),
					prop1.getProperty(ConstantesCoreUtil.KEY_FILE_PROPERTIES_OFV));
			return prop2.getProperty(llave);
		} catch (Exception e) {
			Logs.error("No ha sido posible leer la propiedad " + llave + " en archivos de propiedades >> ", e);
			throw new CustomRuntimeException(
					"No fue posible cargar la propiedad solicitada de los archivos de propiedades");
		}
	}

}
