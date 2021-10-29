package com.alianza.lib.configs;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alianza.lib.dto.ConfiguracionesFtpDTO;
import com.alianza.lib.utilitarios.PropertiesUtil;
import com.alianza.lib.utilitarios.ConstantesCoreUtil.ConstantesFTP;
import com.jcraft.jsch.ChannelSftp;

import lombok.Data;

/**
 * Clase que configura las propiedades SFTP y el pool de conexiones
 *
 * @version 1.0.0
 */

@Data
@ConfigurationProperties(prefix = "sftp")
public class SFTPConfigProperties {

    private ConfiguracionesFtpDTO configuracionesFtp = new ConfiguracionesFtpDTO();
    private ConfiguracionPool configuracionPool = new ConfiguracionPool();

    /**
     * Constructor con las propiedades de conexión SFTP
     */
    public SFTPConfigProperties() {
        configuracionesFtp.setServer(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_SERVER));
        configuracionesFtp
                .setPort(Integer.parseInt(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PORT)));
        configuracionesFtp.setUsername(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_USER));
        configuracionesFtp.setPassword(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PASS));
        configuracionesFtp.setPathLocalAnexos(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_LOCAL_ANEXOS));
        configuracionesFtp.setPathRemotoFC(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_ABSOLUTO_FONDO_COMUN));
        configuracionesFtp.setPathLocalFC(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_LOCAL_FONDO_COMUN));
        configuracionesFtp.setNombreArchivoFC(PropertiesUtil
                .obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_NOMBRE_ARCHIVO_FONDO_COMUN));
        configuracionesFtp.setNombreArchivoErrorFC(PropertiesUtil
                .obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_NOMBRE_ARCHIVO_ERROR_FONDO_COMUN));
        configuracionesFtp.setExtensionArchivo(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_EXTENSION_DOCX));
        configuracionesFtp.setExtensionArchivoErrorFC(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_EXTENSION_PDF));
        configuracionesFtp.setPathLocalContratos(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_LOCAL_CONTRATOS));
        configuracionesFtp.setPathRemotoContratos(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_ABSOLUTO_CONTRATOS));
        configuracionesFtp.setNombreContratoPreVinculacion(PropertiesUtil
                .obtenerPropiedad(ConstantesFTP.CONST_PREFIJO_ARCHIVO_CONTRATO_PREVINCULACION));
        configuracionesFtp.setNombreContratoVinculacion(PropertiesUtil
                .obtenerPropiedad(ConstantesFTP.CONST_PREFIJO_ARCHIVO_CONTRATO_VINCULACION));
        configuracionesFtp.setNombreContratoCompraVenta(PropertiesUtil
                .obtenerPropiedad(ConstantesFTP.CONST_PREFIJO_ARCHIVO_CONTRATO_COMPRAVENTA));
        configuracionesFtp.setPathLocalFormularioVinculacion(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_LOCAL_FORMULARIO_VINCULACION));
        configuracionesFtp.setPathLocalPlanPagos(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_LOCAL_PLAN_PAGOS));
        configuracionesFtp.setPathRemotoFormularioVinculacion(PropertiesUtil
                .obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_ABSOLUTO_FORMULARIO_VINCULACION));
        configuracionesFtp.setPathRemotoPlanPagos(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_ABSOLUTO_PLAN_PAGOS));
        configuracionesFtp.setNombreFormularioVinculacion(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PREFIJO_FORMULARIO_VINCULACION));
        configuracionesFtp.setNombrePlanPagos(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PREFIJO_PLAN_PAGOS));
        configuracionesFtp.setPathRemotoFCO(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PATH_REMOTO_ABSOLUTO_FCO));
        configuracionesFtp.setNombreFondoComun(
                PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_PREFIJO_FONDO_COMUN));
    }

    /**
     * Clase que configura el pool de conexiones
     */
    public static class ConfiguracionPool extends GenericObjectPoolConfig<ChannelSftp> {

        private int maxTotal = Integer.parseInt(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_MAX_TOTAL_POOL));
        private int maxIdle = Integer.parseInt(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_MAX_IDLE_POOL));
        private int minIdle = Integer.parseInt(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_MIN_IDLE_POOL));
        private boolean jmxEnabled = Boolean.parseBoolean(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_JMXENABLED));
        private boolean testWhileIdle = Boolean.parseBoolean(PropertiesUtil.obtenerPropiedad(ConstantesFTP.CONST_TESTWHILEIDLE));

        public ConfiguracionPool(){
            super();
        }

        @Override
        public int getMaxTotal() {
            return maxTotal;
        }

        @Override
        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        @Override
        public int getMaxIdle() {
            return maxIdle;
        }

        @Override
        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        @Override
        public int getMinIdle() {
            return minIdle;
        }

        @Override
        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public boolean isJmxEnabled() {
            return jmxEnabled;
        }

        @Override
        public void setJmxEnabled(boolean jmxEnabled) {
            this.jmxEnabled = jmxEnabled;
        }

        public boolean isTestWhileIdle() {
            return testWhileIdle;
        }

        @Override
        public void setTestWhileIdle(boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
        }
    }

}
