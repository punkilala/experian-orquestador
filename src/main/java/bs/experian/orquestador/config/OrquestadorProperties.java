package bs.experian.orquestador.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "experian")
public class OrquestadorProperties {

    private Api api;

    @Getter
    @Setter
    public static class Api {

        private Integracion integracion;
        private Integer timeoutrequest;
        private Integer timeoutresponse;
    }

    @Getter
    @Setter
    public static class Integracion {

        private String baseUrl;
        private String experianSolicitudesUrl;
        private String experianDescargaDocumentoUrl;
    }

}