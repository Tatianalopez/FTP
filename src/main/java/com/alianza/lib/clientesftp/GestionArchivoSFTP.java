package com.alianza.lib.clientesftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alianza.lib.clientesftp.impl.IGestionArchivos;
import com.alianza.lib.clientesftp.impl.IValidacionArchivos;
import com.alianza.lib.excepciones.CustomApplicationException;
import com.alianza.lib.excepciones.CustomRuntimeException;
import com.alianza.lib.pool.ConexionPool;
import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesConexionFTP;
import com.alianza.lib.utilitarios.Logs;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GestionArchivoSFTP implements IGestionArchivos {

	private final ConexionPool conexionPool;
	
	@Autowired
	private IValidacionArchivos validateFile;

	public GestionArchivoSFTP(ConexionPool conexionPool) {
		this.conexionPool = conexionPool;
	}

	@Override
	public synchronized InputStream getFTP(String remoto) {
		log.info("Inicia InputStream getFTP. remoto: " + remoto);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		String remotoCompleto = validateFile.obtenerRutaBase() + remoto;
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log.info(ConstantesConexionFTP.CONST_CONEXION_OBTENIDA);
		try {
			InputStream in = channelSftp.get(remotoCompleto);
			log.info("InputStream getFTP. Descarga de archivo del STFP satisfactoriamente. in:" + in
					+ " remotoCompleto: " + remotoCompleto);
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
			log.info(ConstantesConexionFTP.CONST_CONEXION_DEVUELTA);
			log.info("Fin InputStream getFTP.");
		}
	}

	@Override
	public synchronized boolean putFTP(String remoto, InputStream enMemoria) throws CustomApplicationException {
		log.info("Inicia boolean putFTP. remoto: " + remoto + " enMemoria: " + enMemoria);
		remoto = validateFile.obtenerRutaBase() + remoto;
		remoto = remoto.replace("\\", "/");
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log.info(ConstantesConexionFTP.CONST_CONEXION_OBTENIDA);
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
			log.info(ConstantesConexionFTP.CONST_CONEXION_DEVUELTA);
			log.info("Fin boolean putFTP");
		}
	}

	@Override
	public synchronized boolean deleteArchivoDelFTP(String rutaArchivo) throws CustomApplicationException {
		log.info("Inicia boolean deleteArchivoDelFTP. rutaArchivo: " + rutaArchivo);
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log.info(ConstantesConexionFTP.CONST_CONEXION_OBTENIDA);
		try {
			rutaArchivo = validateFile.obtenerRutaBase() + rutaArchivo;
			if (validateFile.comprobarSiLaRutaEsUnArchivo(rutaArchivo) && existsSFTPRemoteFileOrDirectory(rutaArchivo)) {
				channelSftp.rm(rutaArchivo);
				log.info("boolean deleteArchivoDelFTP. Archivo eliminado correctamente. rutaArchivo: " + rutaArchivo);
			} else {
				log.error(
						"boolean deleteArchivoDelFTP. No fue posible encontrar el archivo que intenta eliminar en la ruta especificada: "
								+ rutaArchivo);
				throw new SftpException(ChannelSftp.SSH_FX_NO_SUCH_FILE,
						"No fue posible encontrar el archivo que intenta eliminar en la ruta especificada.");
			}
			return true;
		} catch (Exception e) {
			log.error("boolean deleteArchivoDelFTP. Exception e. Error en eliminación archivo. e.getMessage(): "
					+ e.getMessage());
			log.error(e.getMessage());
			throw new CustomApplicationException(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log.info(ConstantesConexionFTP.CONST_CONEXION_DEVUELTA);
			log.info("Fin boolean deleteArchivoDelFTP.");
		}
	}

	@Override
	public synchronized boolean existsSFTPRemoteFileOrDirectory(String currentPath) {
		log.info("Inicia boolean existsSFTPRemoteFileOrDirectory. currentPath: " + currentPath);
		boolean ackExists = false;
		ChannelSftp channelSftp = this.conexionPool.getBorrowObject();
		log.info(ConstantesConexionFTP.CONST_CONEXION_OBTENIDA);
		try {
			@SuppressWarnings("unchecked")
			List<ChannelSftp.LsEntry> lsCmdResults = new ArrayList<>(channelSftp.ls(currentPath));
			log.info("boolean existsSFTPRemoteFileOrDirectory. Se obtiene información del path. lsCmdResults: "
					+ lsCmdResults);
			ackExists = !lsCmdResults.isEmpty();
		} catch (SftpException e) {
			log.error("boolean existsSFTPRemoteFileOrDirectory. SftpException e. e.getMessage(): " + e.getMessage());
			log.error(e.getMessage());
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				log.error(
						"boolean existsSFTPRemoteFileOrDirectory. No se encontró el archivo en la ruta especificada.");
			} else {
				log.error(
						"boolean existsSFTPRemoteFileOrDirectory. Error inesperado al ejecutar comando remoto ls en sftp: [{}:{}]",
						e.id, e.getMessage());
			}
		} catch (Exception e) {
			log.error("boolean existsSFTPRemoteFileOrDirectory. Exception e. e.getMessage(): " + e.getMessage());
			log.error(e.getMessage());
		} finally {
			this.conexionPool.returnObject(channelSftp);
			log.info(ConstantesConexionFTP.CONST_CONEXION_DEVUELTA);
		}
		log.info("Fin boolean existsSFTPRemoteFileOrDirectory ackExists: " + ackExists);
		return ackExists;

	}

}
