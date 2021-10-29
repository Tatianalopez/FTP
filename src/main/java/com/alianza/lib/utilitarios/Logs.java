package com.alianza.lib.utilitarios;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Class para el inicio del logging.
 * 
 * @author Sophos Banking Solutions
 * @version 1.0.0
 */
@Slf4j
public class Logs {

	private Logs() {
	    throw new IllegalStateException("Logs class is a static class");
	}

    /** Entidad Iniciador */
	public static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

	/** Entidad Log Informativo a nivel debug */
	public static final Logger LOG_DEBUG = LoggerFactory.getLogger("");
	
    /** Entidad Log Errores a nivel error */
	public static final Logger LOG_ERRORES = LoggerFactory.getLogger("fileloggerErrores");
		
	/** Entity Log Debugger outputting to stdout at debug level */
	public static final Logger LOG_CONSOLE = LoggerFactory.getLogger("consoleloggerOutput");
	
	/** Entity Log Mail outputting to stdout at debug level */
	public static final Logger LOG_MAIL = LoggerFactory.getLogger("fileloggerMail");
	
	public static final void trace(String... message) {
		String formattedStr = StringUtil.concat(message);
		LOG_DEBUG.debug(formattedStr);
		LOG_CONSOLE.trace(formattedStr);
	}
		
	public static final void trace(String msg, Throwable t) {
		LOG_DEBUG.debug(msg, t);
		LOG_CONSOLE.trace(msg, t);
	}
	
	public static final void trace(Marker marker, String msg, Throwable t) {
		LOG_DEBUG.debug(marker, msg, t);
		LOG_CONSOLE.trace(marker, msg, t);
	}
	
	public static final void debug(String... message) {
		String formattedStr = StringUtil.concat(message);
		LOG_DEBUG.debug(formattedStr);
		LOG_CONSOLE.debug(formattedStr);
	}
	
	public static final void debug(String message, Object... args) {
		String formattedStr = StringUtil.concat(message, args);
		LOG_DEBUG.debug(formattedStr);
		LOG_CONSOLE.debug(formattedStr);
	}
	
	public static final void debug(String msg, Throwable t) {
		LOG_DEBUG.debug(msg, t);
		LOG_CONSOLE.debug(msg, t);
	}
	
	public static final void debug(Marker marker, String msg, Throwable t) {
		LOG_DEBUG.debug(marker, msg, t);
		LOG_CONSOLE.debug(marker, msg, t);
	}
	
	public static final void info(String... message) {
		String formattedStr = StringUtil.concat(message);
		LOG_DEBUG.info(formattedStr);
		LOG_CONSOLE.info(formattedStr);
	}
	
	public static final void info(String message, Object... args) {
		String formattedStr = StringUtil.concat(message, args);
		LOG_DEBUG.info(formattedStr);
		LOG_CONSOLE.info(formattedStr);
	}
	
	public static final void info(String msg, Throwable t) {
		LOG_DEBUG.info(msg, t);
		LOG_CONSOLE.info(msg, t);
	}
	
	public static final void info(Marker marker, String msg, Throwable t) {
		LOG_DEBUG.info(marker, msg, t);
		LOG_CONSOLE.info(marker, msg, t);
	}
	
	public static final void warn(String... message) {
		String formattedStr = StringUtil.concat(message);
		LOG_DEBUG.warn(formattedStr);
		LOG_ERRORES.warn(formattedStr);
		LOG_CONSOLE.warn(formattedStr);
	}
	
	public static final void warn(String message, Object... args) {
		String formattedStr = StringUtil.concat(message, args);
		LOG_DEBUG.warn(formattedStr);
		LOG_ERRORES.warn(formattedStr);
		LOG_CONSOLE.warn(formattedStr);
	}
	
	public static final void warn(String msg, Throwable t) {
		LOG_DEBUG.warn(msg, t);
		LOG_ERRORES.warn(msg, t);
		LOG_CONSOLE.warn(msg, t);
	}
	
	public static final void warn(Marker marker, String msg, Throwable t) {
		LOG_DEBUG.warn(marker, msg, t);
		LOG_ERRORES.warn(marker, msg, t);
		LOG_CONSOLE.warn(marker, msg, t);
	}
	
	public static final void error(String... message) {
		String formattedStr = StringUtil.concat(message);
		LOG_DEBUG.error(formattedStr);
		LOG_ERRORES.error(formattedStr);
		LOG_CONSOLE.error(formattedStr);
	}
	
	public static final void error(String message, Object... args) {
		String formattedStr = StringUtil.concat(message, args);
		LOG_DEBUG.error(formattedStr);
		LOG_ERRORES.error(formattedStr);
		LOG_CONSOLE.error(formattedStr);
	}
	
	public static final void error(String msg, Throwable t) {
		LOG_DEBUG.error(msg, t);
		LOG_ERRORES.error(msg, t);
		LOG_CONSOLE.error(msg, t);
	}
	
	public static final void error(Marker marker, String msg, Throwable t) {
		LOG_DEBUG.error(marker, msg, t);
		LOG_ERRORES.error(marker, msg, t);
		LOG_CONSOLE.error(marker, msg, t);
	}
	
	public static final void error(Exception e) {	
		String formattedStr = StringUtil.concat(e.getMessage(), ". Traza del error: ", Arrays.toString(e.getStackTrace()));
		LOG_DEBUG.error(formattedStr);
		LOG_ERRORES.error(formattedStr);
		LOG_CONSOLE.error(formattedStr);
	}
	
	public static final void infoMail(String... message) {
		String formattedStr = StringUtil.concat(message);
		LOG_MAIL.info(formattedStr);
	}
}