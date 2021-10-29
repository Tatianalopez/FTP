package com.alianza.lib.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.alianza.lib.conexionsftp.ConexionSFTP;
import com.alianza.lib.excepciones.CustomRuntimeException;
import com.alianza.lib.utilitarios.Logs;
import com.jcraft.jsch.ChannelSftp;
import lombok.Data;

/**
 * Clase para crear conexión del pool
 *
 * @version 1.0.0
 */

@Data
public class ConexionPool {

    private GenericObjectPool<ChannelSftp> genericObjectPool;
    private static final String CONST_ERROR_CONEXION_POOL = "Error obtener conexión Pool: ";

    public ConexionPool(ConexionSFTP conexionSFTP) {
        this.genericObjectPool = new GenericObjectPool<>(conexionSFTP, conexionSFTP.getSftpConfigProperties().getConfiguracionPool());
    }

    /**
     * Método encargado de obtener un objeto de conexión SFTP
     * @return ChannelSftp
     */
    public ChannelSftp getBorrowObject() {
        try {
            Logs.info("Obtener objeto de conexión SFTP");
            return this.genericObjectPool.borrowObject();
        } catch (Exception e) {
            Logs.error(e);
            throw new CustomRuntimeException(CONST_ERROR_CONEXION_POOL + e.getMessage());
        }
    }

    /**
     * Método encargado de devolver el objeto de conexión SFTP
     * @param channelSftp
     */
    public void returnObject(ChannelSftp channelSftp) {
        if(channelSftp != null) {
            this.genericObjectPool.returnObject(channelSftp);
            Logs.info("Devuelve objeto de conexión SFTP");
        }
    }

}
