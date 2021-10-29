package com.alianza.lib.dto;

import lombok.Data;

@Data
public class ConfiguracionesFtpDTO {
	private String server;
	private int port;
	private String username;
	private String password;
	private String pathRemotoFC;
	private String pathLocalFC;
	private String pathLocalAnexos;
	private String nombreArchivoFC;
	private String nombreArchivoErrorFC;
	private String extensionArchivo;
	private String extensionArchivoErrorFC;
	private String pathLocalArchivos;
	private String pathLocalContratos;
	private String pathRemotoContratos;
	private String nombreContratoPreVinculacion;
	private String nombreContratoVinculacion;
	private String nombreContratoCompraVenta;
	private String pathLocalFormularioVinculacion;
	private String pathRemotoFormularioVinculacion;
	private String pathLocalPlanPagos;
	private String pathRemotoPlanPagos;
	private String nombreFormularioVinculacion;
	private String nombrePlanPagos;
	private String pathRemotoFCO;
	private String nombreFondoComun;
	
}
