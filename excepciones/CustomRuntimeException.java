package com.alianza.ofv.core.excepciones;

import com.alianza.ofv.core.utilitarios.Logs;

public class CustomRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private static String mensaje;

	public CustomRuntimeException(String... exceptionMsg) {
		super(llenarMensaje(exceptionMsg));
	}

	private static String llenarMensaje(String... exceptionMsg) {
		mensaje = "";
		for (String data : exceptionMsg) {
			mensaje = mensaje + data;
		}
		Logs.LOG_ERRORES.error(mensaje);
		return mensaje;

	}
}