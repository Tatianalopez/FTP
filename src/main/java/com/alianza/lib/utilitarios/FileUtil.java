package com.alianza.lib.utilitarios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.alianza.lib.excepciones.CustomRuntimeException;

public class FileUtil {

	public static byte[] inputstream(String pathFile) {
		try {
			return Files.readAllBytes(Paths.get(pathFile));
		} catch (IOException e) {
			 throw new CustomRuntimeException("No se pudo abrir del servidor el archivo ' ", pathFile);
		}
	}

	public static void crearDirectorio(Path filaLocal) {
		if (!filaLocal.toFile().exists()) {
			try {
				Files.createDirectory(filaLocal);
			} catch (IOException e) {
				throw new CustomRuntimeException("No se pudo crear en el servidor el directorio '", filaLocal + "'");
			}
		}
	}

	/**
	 * @deprecated use {@link #deleteFile(String)}
	 * @param pathFile
	 */
	public static void deleteLinux(String pathFile) {
		File f = new File(pathFile);
		try {
			java.lang.Runtime.getRuntime().exec("rm -f " + f.getAbsolutePath());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @deprecated use {@link #deleteFile(String)}
	 * @param pathFile
	 * @return
	 */
	public static boolean deleteWin(String pathFile) {
		File f = new File(pathFile);
		boolean isSuccessful = f.delete();
		return isSuccessful;
	}

	/**
	 * Elimina el archivo corespondiente.<br>
	 * este metodo identifica el SO en el cual se desea eliminar el archivo. De ese
	 * modo ejecuta el comando mas adecuado
	 * 
	 * @param pathFile
	 */
	public static void deleteFile(String pathFile) {
		if (!esLinux()) {
			deleteWin(pathFile);
		} else {
			deleteLinux(pathFile);
		}

	}

	public static Boolean esLinux() {
		Boolean esLinux = null;
		String oS = System.getProperty("os.name").toLowerCase();
		if ((oS.indexOf("win") >= 0)) {
			esLinux = Boolean.FALSE;
		}
		if (oS.indexOf("nix") >= 0 || oS.indexOf("nux") >= 0 || oS.indexOf("aix") > 0) {
			esLinux = Boolean.TRUE;
		}
		return esLinux;

	}

}