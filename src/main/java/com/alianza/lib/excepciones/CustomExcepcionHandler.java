package com.alianza.lib.excepciones;

public class CustomExcepcionHandler {

	private String mensajeErrores;
	 
    public CustomExcepcionHandler(String errorMessage){
        this.mensajeErrores = errorMessage;
    }
 
    public String getErrorMessage() {
        return mensajeErrores;
    }

}
