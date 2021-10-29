package com.alianza.lib.clientesftp;

import java.io.File;

import com.alianza.lib.clientesftp.impl.IValidacionArchivos;
import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesFTP;
import com.alianza.lib.utilitarios.PropertiesUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidacionArchivos implements IValidacionArchivos {

	@Override
	public String obtenerRutaBase() {
		log.info("Inicia - Fin String obtenerRutaBase(). PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_FTP_HOME: "
				+ PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_FTP_HOME));
		return PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_FTP_HOME);
	}

	@Override
	public boolean comprobarSiLaRutaEsUnArchivo(String rutaArchivo) {
		log.info("Inicia - Fin boolean comprobarSiLaRutaEsUnArchivo. rutaArchivo: " + rutaArchivo);
		return new File(rutaArchivo).getName().contains(".");
	}

}
