package com.alianza.lib.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alianza.lib.utilitarios.Logs;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CustomBadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static String mensaje;

    public CustomBadRequestException(String... exceptionMsg) {
        super(llenarMensaje(exceptionMsg));
        this.mensaje = mensaje;
    }

    private static String llenarMensaje(String... exceptionMsg) {
        mensaje = "";
        for (String data : exceptionMsg) {
            mensaje = mensaje + data;
        }
        Logs.LOG_ERRORES.error(mensaje);
        return mensaje;
    }

    public static String getMensaje() {
        return mensaje;
    }

    public static void setMensaje(String mensaje) {
        CustomBadRequestException.mensaje = mensaje;
    }
}
