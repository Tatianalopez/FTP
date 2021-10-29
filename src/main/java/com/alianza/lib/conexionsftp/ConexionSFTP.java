package com.alianza.lib.conexionsftp;

import com.alianza.lib.configs.SFTPConfigProperties;
import com.alianza.lib.excepciones.CustomApplicationException;
import com.alianza.lib.excepciones.CustomRuntimeException;
import com.alianza.lib.servicios.CorreoESrv;
import com.alianza.lib.utilitarios.Logs;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

/**
 * Clase para crear conexión al servidor SFTP
 *
 * @version 1.0.0
 */

@Data
public class ConexionSFTP extends BasePooledObjectFactory<ChannelSftp> {

    private SFTPConfigProperties sftpConfigProperties;
    private static final String CONST_ERROR_CONEXION = "Error de conexión: ";

    @Autowired
    private CorreoESrv correoESrv;

    public ConexionSFTP(SFTPConfigProperties sftpConfigProperties){
        this.sftpConfigProperties = sftpConfigProperties;
    }

    /**
     * Método encargado de realizar la conexión al servidor SFTP.
     * @return ChannelSftp
     */
    @Override
    public ChannelSftp create() throws Exception {
        try {
            Logs.info("Iniciando conexión SFTP.");
            JSch jsch = new JSch();
            Session session = jsch.getSession(
                    this.sftpConfigProperties.getConfiguracionesFtp().getUsername(),
                    this.sftpConfigProperties.getConfiguracionesFtp().getServer(),
                    this.sftpConfigProperties.getConfiguracionesFtp().getPort());
            session.setPassword(this.sftpConfigProperties.getConfiguracionesFtp().getPassword());
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.setConfig(config);
            session.connect();
            Logs.info("Conectado al SFTP.");
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            Logs.info("Canal SFTP abierto.");
            return channelSftp;
        } catch (Exception e) {
            Logs.error(e);
            try {
                correoESrv.enviar("Conectar FTP", e);
            } catch (CustomApplicationException e1) {
                Logs.error(e1);
            }
            throw new CustomRuntimeException(CONST_ERROR_CONEXION, e.getMessage());
        }
    }

    /**
     * Método encargado de crear DefaultPooledObject.
     * @param  channelSftp
     */
    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channelSftp) {
        Logs.info("Inicia wrap.");
        return new DefaultPooledObject<>(channelSftp);
    }

    /**
     * Método encargado de desconectar el canal.
     * @param pooledObject
     */
    @Override
    public void destroyObject(PooledObject<ChannelSftp> pooledObject){
        Logs.info("Inicia destroyObject");
        ChannelSftp channelSftp =pooledObject.getObject();
        Logs.info("Obtiene channelSftp en destroyObject");
        channelSftp.disconnect();
        Logs.info("Desconecta channelSftp en destroyObject");
    }

}
