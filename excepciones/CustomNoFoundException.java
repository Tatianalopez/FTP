package com.alianza.ofv.core.excepciones;

import com.alianza.ofv.core.utilitarios.Logs;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomNoFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static String mensaje;

    public CustomNoFoundException(String... exceptionMsg){
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
        CustomNoFoundException.mensaje = mensaje;
    }
}
