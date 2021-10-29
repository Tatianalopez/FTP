package com.alianza.lib.servicios;

import java.util.Queue;

import com.alianza.lib.dto.MensajeCorreoEDto;
import com.alianza.lib.excepciones.CustomApplicationException;

public interface CorreoESrv {

	/**
	 * Envia correo electronico según parametros.
	 * 
	 * @param asuntoP    asunto del correo
	 * @param saludoP    saludo
	 * @param cuerpoP    cuerpo o mensaje
	 * @param despedidaP despedida
	 * @param toP        Para
	 * @return Boolean si se pudo o no
	 * @throws CustomApplicationException
	 */
	Boolean enviar(String asuntoP, String saludoP, String cuerpoP, String despedidaP, String toP)
			throws CustomApplicationException;
	
	Boolean enviar(MensajeCorreoEDto mailMessage) throws CustomApplicationException;

	/**
	 * Envia listado de correos en cola
	 * 
	 * @param mailMessages
	 * @return
	 * @throws CustomApplicationException
	 */
	Boolean enviar(Queue<MensajeCorreoEDto> mailMessages) throws CustomApplicationException;

	/**
	 * Envia correo electrónico para informar sobre un error que se presento en uno
	 * de los servicios.
	 * 
	 * @param nombreSrvQueFalla nombre del servicio que ha fallado.
	 * @param errorANotificar   error que devolvió el servicio.
	 * @return Boolean si se pudo o no
	 * @throws CustomApplicationException
	 */
	Boolean enviar(String nombreSrvQueFalla, Exception errorANotificar) throws CustomApplicationException;
}
