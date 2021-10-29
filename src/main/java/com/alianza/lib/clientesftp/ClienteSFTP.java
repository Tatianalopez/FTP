package com.alianza.lib.clientesftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.IOUtils;

import com.alianza.lib.configs.SFTPConfigProperties;
import com.alianza.lib.dto.ConfiguracionesFtpDTO;
import com.alianza.lib.excepciones.CustomApplicationException;
import com.alianza.lib.excepciones.CustomRuntimeException;
import com.alianza.lib.pool.ConexionPool;
import com.alianza.lib.utilitarios.HelperUtil;
import com.alianza.lib.utilitarios.Logs;
import com.alianza.lib.utilitarios.PropertiesUtil;
import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesFTP;
import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesSimbolos;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * Operaciones servidor SFTP
 *
 * @version 1.0.0
 */

public class ClienteSFTP {

	private static final String CONST_CONEXION_OBTENIDA = "Se obtiene conexión servidor SFTP";
	private static final String CONST_CONEXION_DEVUELTA = "Se devuelve conexión servidor SFTP";
	private static final String CONST_LOCAL_TEXTO = " local: ";
	private static final String CONST_REMOTO_TEXTO = " remoto: ";
	private static final Integer CONST_INFO_LOG = 1;
	private static final Integer CONST_ERROR_LOG = 2;
	private final ConexionPool conexionPool;
	private final SFTPConfigProperties sftpConfigProperties = new SFTPConfigProperties();

	public ClienteSFTP(ConexionPool conexionPool) {
		this.conexionPool = conexionPool;
	}

	private void log(String message, int type) {
		if (Logs.LOG_DEBUG.isInfoEnabled() && type == CONST_INFO_LOG) {
			Logs.LOG_DEBUG.info(message);
		} else {
			if (Logs.LOG_ERRORES.isErrorEnabled())
				Logs.LOG_ERRORES.error(message);
		}
	}

	/**
	 * Crear un ruta en FTP, valida que la ruta a crear no sea un archivo
	 *
	 * @param nombrePath String con el directorio completo a crear
	 * @return Entrega true si el proceso es exitoso
	 */
	public synchronized boolean crearJerarquiaDeDirectorio(String nombrePath) {
		log(MessageFormat.format("Inicia boolean crearJerarquiaDeDirectorio. nombrePath: {0}", nombrePath),
				CONST_INFO_LOG);
		if (comprobarSiExisteDirectorio(new File(nombrePath))) {
			log("boolean crearJerarquiaDeDirectorio. comprobarSiExisteDirectorio false", CONST_INFO_LOG);
			return Boolean.FALSE;
		}
		String nombrePath2 = this.obtenerRutaBase() + ConstantesSimbolos.SLASH
				+ nombrePath.replace(ConstantesSimbolos.BACKSLASH, ConstantesSimbolos.SLASH);
		log(MessageFormat.format("boolean crearJerarquiaDeDirectorio. Nombre path: {0}", nombrePath2), CONST_INFO_LOG);
		String[] carpetas = nombrePath2.split(ConstantesSimbolos.SLASH);
		StringBuilder currentPath = new StringBuilder();
		for (String temp : carpetas) {
			currentPath.append(ConstantesSimbolos.SLASH).append(temp);
			openFtpDirectory(currentPath.toString());
			log("boolean crearJerarquiaDeDirectorio. String temp: " + temp, CONST_INFO_LOG);
		}
		log("Fin boolean crearJerarquiaDeDirectorio. true", CONST_INFO_LOG);
		return Boolean.TRUE;
	}

	/**
	 * Método encargado de transportar un archivo del SFTP y dejarlo en una ruta
	 * local del servidor.
	 *
	 * @param remoto Ruta donde se encuentra el archivo en el SFTP.
	 * @param local  Ruta donde se dejará el archivo en el servidor.
	 * @return Boolean Flag que indica si se pudo realizar la transferencia o no.
	 */
	public synchronized boolean getFTP(String remoto, String local) {
		log("Inicia boolean getFTP. remoto: " + remoto + CONST_LOCAL_TEXTO + local, CONST_INFO_LOG);
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			remoto = this.obtenerRutaBase() + remoto;
			channelSftp.get(remoto, local);
			log("boolean getFTP. Descarga de archivo del SFTP satisfactoriamente. remoto: " + remoto, CONST_INFO_LOG);
			return true;
		} catch (Exception e) {
			log("boolean getFTP. Exception e. e.getMessage(): " + e.getMessage(), CONST_ERROR_LOG);
			throw new CustomRuntimeException(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin boolean getFTP.", CONST_INFO_LOG);
		}
	}

	/**
	 * Método encargado de obtener un archivo del SFTP y devolverlo como un objeto
	 * de tipo ByteArrayOutputStream.
	 *
	 * @param remoto Ruta en donde se encuentra el archivo a ser transferido.
	 * @return ByteArrayOutputStream Objeto que representa la información del
	 *         archivo.
	 */
	public synchronized ByteArrayOutputStream getFTP(String remoto, int a) {
		log("Inicia ByteArrayOutputStream getFTP. remoto: " + remoto + " a: " + a, CONST_INFO_LOG);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			remoto = this.obtenerRutaBase() + remoto;
			remoto = remoto.replace("\\", "/");
			InputStream is = channelSftp.get(remoto);
			log("ByteArrayOutputStream getFTP. Descarga de archivo del SFTP satisfactoriamente. remoto: " + remoto
					+ " is: " + is, CONST_INFO_LOG);
			IOUtils.copy(is, baos);
			if (!HelperUtil.isNullOrEmpty(is)) {
				is.close();
			}
			if (!HelperUtil.isNullOrEmpty(baos)) {
				baos.flush();
				baos.close();
			}
			log("ByteArrayOutputStream getFTP: is: " + is + " baos: " + baos, CONST_INFO_LOG);
			return baos;
		} catch (IOException rtEx) {
			log("ByteArrayOutputStream getFTP. IOException rtEx. rtEx.getMessage(): " + rtEx.getMessage(),
					CONST_ERROR_LOG);
			Logs.error(rtEx);
			throw new CustomRuntimeException();
		} catch (Exception e) {
			log("ByteArrayOutputStream getFTP. Exception e. e.getMessage(): " + e.getMessage(), CONST_ERROR_LOG);
			Logs.error(e);
			throw new CustomRuntimeException(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin ByteArrayOutputStream getFTP.", CONST_INFO_LOG);
		}
	}

	
	/**
	 * Método encargado de transportar un archivo del SFTP y dejarlo en una ruta
	 * local del servidor.
	 *
	 * @param remoto Ruta donde se encuentra el archivo en el SFTP.
	 * @param local  Ruta donde se dejará el archivo en el servidor.
	 * @return Boolean Flag que indica si se pudo realizar la transferencia o no.*
	 */
	public synchronized boolean getFTPRootFolder(String remoto, String local) {
		try {
			log("Inicia boolean getFTPRootFolder. remoto: " + remoto + CONST_LOCAL_TEXTO + local, CONST_INFO_LOG);
			lsFolderCopy(remoto, local);
			return Boolean.TRUE;
		} catch (Exception e) {
			log("boolean getFTPRootFolder. Exception e. e.getMessage(): " + e.getMessage(), CONST_ERROR_LOG);
			Logs.error(e);
			throw new CustomRuntimeException(e.getMessage());
		} finally {
			log("Fin boolean getFTPRootFolder.", CONST_INFO_LOG);
		}
	}

	

	

	/**
	 * Método que valida la existencia de un directorio en el servidor de FTP
	 *
	 * @return boolean
	 */
	public synchronized boolean comprobarSiExisteDirectorio(File ruta) {
		log("Inicia boolean comprobarSiExisteDirectorio. ruta: " + ruta, CONST_INFO_LOG);
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			channelSftp.cd(ruta.getPath());
			log("boolean comprobarSiExisteDirectorio. Ingrese al directorio en el SFTP. ruta.getPath(): "
					+ ruta.getPath(), CONST_INFO_LOG);
			return Boolean.TRUE;
		} catch (SftpException e) {
			log("boolean comprobarSiExisteDirectorio. SftpException e. e.getMessage(): " + e.getMessage(),
					CONST_ERROR_LOG);
			Logs.error(e);
			return Boolean.FALSE;
		} catch (Exception rtEx) {
			log("boolean comprobarSiExisteDirectorio. Exception rtE. rtEx.getMessage(): " + rtEx.getMessage(),
					CONST_ERROR_LOG);
			Logs.error(rtEx);
			throw new CustomRuntimeException(rtEx.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin boolean comprobarSiExisteDirectorio.", CONST_INFO_LOG);
		}
	}

	/**
	 * Adecuada los separadores de Filas para FTP
	 *
	 * @return String
	 */
	public String adecuaLosSeparadoresDeFilasParaFTP(String nombrePath) {
		log("Inicia String adecuaLosSeparadoresDeFilasParaFTP. nombrePath: " + nombrePath, CONST_INFO_LOG);
		String nombrePath2 = nombrePath.replace(ConstantesSimbolos.BACKSLASH, ConstantesSimbolos.SLASH);
		if (nombrePath2.indexOf(ConstantesSimbolos.SLASH) != 0) {
			nombrePath2 = ConstantesSimbolos.SLASH + nombrePath2;
		}
		log("Fin String adecuaLosSeparadoresDeFilasParaFTP. nombrePath2: " + nombrePath2, CONST_INFO_LOG);
		return nombrePath2;
	}

	/**
	 * Método que retorna la configuración de la conexión al FTP
	 *
	 * @return ConfiguracionesFtpDTO
	 */
	public ConfiguracionesFtpDTO getConfiguracionesFTpDTO() {
		log("Inicia - Fin ConfiguracionesFtpDTO getConfiguracionesFTpDTO. this.sftpConfigProperties.getConfiguracionesFtp(): "
				+ this.sftpConfigProperties.getConfiguracionesFtp(), CONST_INFO_LOG);
		return this.sftpConfigProperties.getConfiguracionesFtp();
	}

	

	/**
	 * Intenta acceder a dirPath, sino deja traza de la ruta al home del usuario.
	 */
	private synchronized void openFtpDirectory(String dirPath) {
		log("Inicia void openFtpDirectory. dirPath: " + dirPath, CONST_INFO_LOG);
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			channelSftp.cd(dirPath);
			log("void openFtpDirectory. Ingrese al directorio en el SFTP.", CONST_INFO_LOG);
		} catch (Exception e) {
			log("void openFtpDirectory. Exception e. e.getMessage(): " + e.getMessage(), CONST_ERROR_LOG);
			Logs.error(e);
			createFtpDirectory(dirPath);
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin void openFtpDirectory.", CONST_INFO_LOG);
		}
	}

	/**
	 * Auxiliar para gestionar la creación de un directorio remoto.
	 */
	private void createFtpDirectory(String dirPath) {
		log("Inicia void createFtpDirectory. dirPath: " + dirPath, CONST_INFO_LOG);
		String homeFtpUserPath = "";
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			homeFtpUserPath = channelSftp.getHome();
			channelSftp.mkdir(dirPath);
			log("void createFtpDirectory. Crea directorio en el SFTP. homeFtpUserPath: " + homeFtpUserPath,
					CONST_INFO_LOG);
		} catch (Exception e) {
			log("void createFtpDirectory. Error en creación del folder. Exception e. dirPath: " + dirPath
					+ " e.getMessage(): " + e.getMessage(), CONST_ERROR_LOG);
			Logs.error(e);
			if (!homeFtpUserPath.isEmpty() && existsSFTPRemoteFileOrDirectory(homeFtpUserPath)) {
				log("void createFtpDirectory !homeFtpUserPath.isEmpty() && existsSFTPRemoteFileOrDirectory(homeFtpUserPath) is true",
						CONST_INFO_LOG);
				openFtpDirectory(homeFtpUserPath);
			}
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin void createFtpDirectory.", CONST_INFO_LOG);
		}
	}

	/**
	 * Verifica si existe en el servidor remoto la ruta especificada.
	 *
	 * @param currentPath ruta a verificar
	 * @return boolean
	 */
	private synchronized boolean existsSFTPRemoteFileOrDirectory(String currentPath) {
		log("Inicia boolean existsSFTPRemoteFileOrDirectory. currentPath: " + currentPath, CONST_INFO_LOG);
		boolean ackExists = false;
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			@SuppressWarnings("unchecked")
			List<ChannelSftp.LsEntry> lsCmdResults = new ArrayList<>(channelSftp.ls(currentPath));
			log("boolean existsSFTPRemoteFileOrDirectory. Se obtiene información del path. lsCmdResults: "
					+ lsCmdResults, CONST_INFO_LOG);
			ackExists = !lsCmdResults.isEmpty();
		} catch (SftpException e) {
			log("boolean existsSFTPRemoteFileOrDirectory. SftpException e. e.getMessage(): " + e.getMessage(),
					CONST_ERROR_LOG);
			Logs.error(e);
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				Logs.LOG_ERRORES.error(
						"boolean existsSFTPRemoteFileOrDirectory. No se encontró el archivo en la ruta especificada.");
			} else {
				Logs.LOG_ERRORES.error(
						"boolean existsSFTPRemoteFileOrDirectory. Error inesperado al ejecutar comando remoto ls en sftp: [{}:{}]",
						e.id, e.getMessage());
			}
		} catch (Exception e) {
			log("boolean existsSFTPRemoteFileOrDirectory. Exception e. e.getMessage(): " + e.getMessage(),
					CONST_ERROR_LOG);
			Logs.error(e);
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
		}
		log("Fin boolean existsSFTPRemoteFileOrDirectory ackExists: " + ackExists, CONST_INFO_LOG);
		return ackExists;
	}

	
	/**
	 * Verifica si la ruta es un archivo.
	 *
	 * @param rutaArchivo ruta a verificar
	 * @return boolean
	 */
	private boolean comprobarSiLaRutaEsUnArchivo(String rutaArchivo) {
		log("Inicia - Fin boolean comprobarSiLaRutaEsUnArchivo. rutaArchivo: " + rutaArchivo, CONST_INFO_LOG);
		return new File(rutaArchivo).getName().contains(".");
	}
}
