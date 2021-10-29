package com.alianza.lib.clientesftp.impl;

import org.springframework.stereotype.Service;

@Service
public interface IValidacionArchivos {
	/**
	 * Obtener ruta base FTP
	 *
	 * @return String
	 */
	public String obtenerRutaBase();
		
	/**
	 * Verifica si la ruta es un archivo.
	 *
	 * @param rutaArchivo ruta a verificar
	 * @return boolean
	 */
	public boolean comprobarSiLaRutaEsUnArchivo(String rutaArchivo);
}
