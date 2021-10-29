package com.alianza.lib.clientesftp.impl;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.alianza.lib.excepciones.CustomApplicationException;

@Service
public interface IGestionArchivos {

	/**
	 * trae un archivo del ftp
	 *
	 * @param remoto: ruta del servidor remoto
	 * @return InputStream
	 */
	 InputStream getFTP(String remoto);

	/**
	 * Sube un archivo al FTP, el archivo a subir se encuentra almacenado en memoria
	 * en un InputStream
	 *
	 * @param remoto    String con la ruta de almacenamiento del archivo
	 * @param enMemoria InputStream con la información del archivo a subir
	 * @return Entrega true si el proceso es exitoso
	 */
	boolean putFTP(String remoto, InputStream enMemoria) throws CustomApplicationException;

	/**
	 * Elimina el archivo del FTP siempre y cuando la ruta a eliminar se de un
	 * archivo y no de directorio
	 *
	 * @param rutaArchivo String con la ruta de almacenamiento del archivo
	 * @return Entrega true si el proceso es exitoso
	 */
	boolean deleteArchivoDelFTP(String rutaArchivo) throws CustomApplicationException;

	/**
	 * Verifica si existe en el servidor remoto la ruta especificada.
	 *
	 * @param currentPath ruta a verificar
	 * @return boolean
	 */
	boolean existsSFTPRemoteFileOrDirectory(String currentPath);
}
