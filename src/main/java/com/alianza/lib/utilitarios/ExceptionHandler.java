package com.alianza.lib.utilitarios;


public class ExceptionHandler {
	
	public static final Throwable getRootException(Throwable exception) {
		Throwable rootException = exception;
		while (rootException.getCause() != null && rootException.getCause() != rootException) {
			rootException = rootException.getCause();
		}
		return rootException;
	}
	
}
