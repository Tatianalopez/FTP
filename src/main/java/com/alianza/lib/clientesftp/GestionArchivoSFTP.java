package com.alianza.lib.clientesftp;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alianza.lib.excepciones.CustomApplicationException;
import com.alianza.lib.excepciones.CustomRuntimeException;
import com.alianza.lib.pool.ConexionPool;
import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesFTP;
import com.alianza.lib.utilitarios.HelperUtil;
import com.alianza.lib.utilitarios.Logs;
import com.alianza.lib.utilitarios.PropertiesUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GestionArchivoSFTP {

	private final ConexionPool conexionPool;
	
	public GestionArchivoSFTP(ConexionPool conexionPool) {
		this.conexionPool = conexionPool;
	}
	
	private static final String CONST_CONEXION_OBTENIDA = "Se obtiene conexión servidor SFTP";
	private static final String CONST_CONEXION_DEVUELTA = "Se devuelve conexión servidor SFTP";
	private static final String CONST_LOCAL_TEXTO = " local: ";
	private static final String CONST_REMOTO_TEXTO = " remoto: ";
	/**
	 * trae un archivo del ftp
	 *
	 * @param remoto: ruta del servidor remoto
	 * @return InputStream
	 */
	public synchronized InputStream getFTP(String remoto) {
		log.info("Inicia InputStream getFTP. remoto: " + remoto);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		String remotoCompleto = this.obtenerRutaBase() + remoto;
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log.info(CONST_CONEXION_OBTENIDA);
		try {
			InputStream in = channelSftp.get(remotoCompleto);
			log.info("InputStream getFTP. Descarga de archivo del STFP satisfactoriamente. in:" + in + " remotoCompleto: "
					+ remotoCompleto);
			byte[] buff = new byte[8000];
			int bytesRead;
			while ((bytesRead = in.read(buff)) != -1) {
				bao.write(buff, 0, bytesRead);
				log.info("InputStream getFTP. bytesRead:" + bytesRead);
			}
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bao.toByteArray())) {
				bao.close();
				log.info("InputStream getFTP. bais:" + bais);
				return bais;
			}
		} catch (Exception e) {
			log.error("InputStream getFTP. Exception e. e.getMessage(): " + e.getMessage());
			Logs.error(e);
			throw new CustomRuntimeException(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log.info(CONST_CONEXION_DEVUELTA);
			log.info("Fin InputStream getFTP.");
		}
	}

	/**
	 * Sube un archivo al FTP, el archivo a subir se encuentra almacenado en memoria
	 * en un InputStream
	 *
	 * @param remoto    String con la ruta de almacenamiento del archivo
	 * @param enMemoria InputStream con la información del archivo a subir
	 * @return Entrega true si el proceso es exitoso
	 */
	public synchronized boolean putFTP(String remoto, InputStream enMemoria) throws CustomApplicationException {
		log("Inicia boolean putFTP. remoto: " + remoto + " enMemoria: " + enMemoria, CONST_INFO_LOG);
		remoto = this.obtenerRutaBase() + remoto;
		remoto = remoto.replace("\\", "/");
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log.info(CONST_CONEXION_OBTENIDA);
		log.info("boolean putFTP. remoto: " + remoto);
		try {
			channelSftp.put(enMemoria, remoto);
			log.info("boolean putFTP. El archivo subió satisfactoriamente.");
			return existsSFTPRemoteFileOrDirectory(remoto);
		} catch (Exception e) {
			log.error("boolean putFTP. Exception e. Error en carga de archivo. e.getMessage(): " + e.getMessage());
			Logs.error(e);
			throw new CustomApplicationException(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log.info(CONST_CONEXION_DEVUELTA);
			log.info("Fin boolean putFTP");
		}
	}

	/**
	 * Elimina el archivo del FTP siempre y cuando la ruta a eliminar se de un
	 * archivo y no de directorio
	 *
	 * @param rutaArchivo String con la ruta de almacenamiento del archivo
	 * @return Entrega true si el proceso es exitoso
	 */
	public synchronized boolean deleteArchivoDelFTP(String rutaArchivo) throws CustomApplicationException {
		log("Inicia boolean deleteArchivoDelFTP. rutaArchivo: " + rutaArchivo, CONST_INFO_LOG);
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			rutaArchivo = this.obtenerRutaBase() + rutaArchivo;
			if (comprobarSiLaRutaEsUnArchivo(rutaArchivo) && existsSFTPRemoteFileOrDirectory(rutaArchivo)) {
				channelSftp.rm(rutaArchivo);
				log("boolean deleteArchivoDelFTP. Archivo eliminado correctamente. rutaArchivo: " + rutaArchivo,
						CONST_INFO_LOG);
			} else {
				log("boolean deleteArchivoDelFTP. No fue posible encontrar el archivo que intenta eliminar en la ruta especificada: "
						+ rutaArchivo, CONST_ERROR_LOG);
				throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE,
						"No fue posible encontrar el archivo que intenta eliminar en la ruta especificada.");
			}
			return true;
		} catch (Exception e) {
			log("boolean deleteArchivoDelFTP. Exception e. Error en eliminación archivo. e.getMessage(): "
					+ e.getMessage(), CONST_ERROR_LOG);
			Logs.error(e);
			throw new CustomApplicationException(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin boolean deleteArchivoDelFTP.", CONST_INFO_LOG);
		}
	}

	/**
	 * Método para obtener lista de archivos en una ruta específica
	 */
	@SuppressWarnings("unchecked")
	private synchronized void lsFolderCopy(String remoto, String local) throws Exception {
		log("Inicia void lsFolderCopy. remoto: " + remoto + CONST_LOCAL_TEXTO + local, CONST_INFO_LOG);
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log(CONST_CONEXION_OBTENIDA, CONST_INFO_LOG);
		try {
			List<ChannelSftp.LsEntry> directories = new ArrayList<>(channelSftp.ls(remoto));
			log("void lsFolderCopy. Se obtiene información del directorio. directories: " + directories,
					CONST_INFO_LOG);
			if (HelperUtil.isNullOrEmpty(directories)) {
				log("void lsFolderCopy. HelperUtil.isNullOrEmpty(directories): " + HelperUtil.isNullOrEmpty(directories)
						+ CONST_REMOTO_TEXTO + remoto, CONST_ERROR_LOG);
				throw new CustomApplicationException(
						"void lsFolderCopy. Error de comunicación con el FTP :: directorio vacío :: ", remoto);
			}
			for (ChannelSftp.LsEntry oListItem : directories) {
				if (!oListItem.getAttrs().isDir()) {
					new File(local + oListItem.getFilename());
					channelSftp.get(remoto + oListItem.getFilename(), local + oListItem.getFilename());
					log("void lsFolderCopy. Descarga de archivo del STFP. local: " + local
							+ " oListItem.getFilename(): " + oListItem.getFilename() + CONST_REMOTO_TEXTO + remoto,
							CONST_INFO_LOG);
				} else if (!".".equals(oListItem.getFilename()) && !"..".equals(oListItem.getFilename())) {
					final boolean mkdirs = new File(local + oListItem.getFilename()).mkdirs();
					log("void lsFolderCopy. Crea directorio. mkdirs: " + mkdirs + CONST_LOCAL_TEXTO + local
							+ " oListItem.getFilename(): " + oListItem.getFilename() + CONST_REMOTO_TEXTO + remoto,
							CONST_INFO_LOG);
					lsFolderCopy(remoto + oListItem.getFilename(), local + oListItem.getFilename());
				}
			}
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log(CONST_CONEXION_DEVUELTA, CONST_INFO_LOG);
			log("Fin void lsFolderCopy.", CONST_INFO_LOG);
		}
	}
	
	/**
	 * Obtener ruta base FTP
	 *
	 * @return String
	 */
	public String obtenerRutaBase() {
		log("Inicia - Fin String obtenerRutaBase(). PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_FTP_HOME: "
				+ PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_FTP_HOME), CONST_INFO_LOG);
		return PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_FTP_HOME);
	}

}
