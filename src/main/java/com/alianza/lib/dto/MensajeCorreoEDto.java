package com.alianza.lib.dto;

import lombok.Data;

@Data
public class MensajeCorreoEDto {

	private String from;

	private String to;
	
	private String[] toArray;

	private String[] cc;
	
	private String[] bcc;
	
	private String asunto;

	private String contenidoMensaje;

	// determina si el contenido del mensaje es HTML, no debe ser null
	private boolean contenidoMensajeHTML;

	
}
