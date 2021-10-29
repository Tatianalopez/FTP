package com.alianza.lib.configs;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alianza.lib.clientesftp.ClienteSFTP;
import com.alianza.lib.conexionsftp.ConexionSFTP;
import com.alianza.lib.pool.ConexionPool;
import com.alianza.lib.utilitarios.Logs;

/**
 * Configuración Beans servidor SFTP
 *
 * @version 1.0.0
 */

@Configuration
@EnableConfigurationProperties(SFTPConfigProperties.class)
public class SFTPConfigBean {

    @Bean
    public ConexionSFTP conexionFTP(SFTPConfigProperties sftpConfigProperties) {
        Logs.debug("Ingresa a ConexionSFTP");
        return new ConexionSFTP(sftpConfigProperties);
    }

    @Bean
    public ConexionPool conexionPool(ConexionSFTP conexionSFTP) {
        Logs.debug("Ingresa a ConexionPool");
        return new ConexionPool(conexionSFTP);
    }

    @Bean
    public ClienteSFTP clienteFTP(ConexionPool conexionPool) {
        Logs.debug("Ingresa a ClienteSFTP");
        return new ClienteSFTP(conexionPool);
    }

}
