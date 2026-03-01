package bs.experian.orquestador.utils;

public final class DomainEnum {

	private DomainEnum() {
        throw new UnsupportedOperationException(DomainEnum.class.getName() + " no instanciable");
    }
	
	//TIPO PERSONA
	 public enum TipoPersona {

        PF("PERSONA_FISICA"),
        PJ("PERSONA_JURIDICA");

        private final String descripcion;

        TipoPersona(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // DESTINO
    public enum Destino {

        PA("PARTICULAR"),
        EM("EMPRESARIAL");

        private final String descripcion;

        Destino(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // INICIO DE CIRCUITO
    public enum InicioCircuito {

        RE("REMOTO"),
        OF("OFICINA");

        private final String descripcion;

        InicioCircuito(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }


    // TIPO DE OPERACIÓN
    public enum TipoOperacion {

        NC("NUEVA_CONCESION"),
        RN("RENOVACION");

        private final String descripcion;

        TipoOperacion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // TIPO DE INTERVENCIÓN
    public enum TipoIntervencion {

        TI("TITULAR"),
        AV("AVALISTA");

        private final String descripcion;

        TipoIntervencion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Estados internos bs Solicitud
    public enum EstadoInterno {

    	CREADA("SOLICITUD_CREADA"),
    	EN_PROCESO("SOLICITUD_CREADA_NOTIFICADA"),
    	CUSTODIA_EN_PROCESO("COMIENZO_DESCARGA_GUARDADO_GD"),
    	CANCELADA("EXPERIAN_CANCELA_SOLICITUD"),
    	ERROR("SOLICITUD_ERROR_EXPERIAN"),
    	CUSTODIA_COMPLETA("TODOS_DOCUMENTOS_GUARDADOS_GD"),
    	CUSTODIA_PARCIAL("ALGUNOS_DOCUMENTOS__NO_GUARDADOS_GD"),
    	ERROR_CUSTODIA("NINGUN_DOCUMENTO_GUARDADO_GD");

        private final String descripcion;

        EstadoInterno(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // para identificar si el evento es de experian o interno
    public enum TiposEventosHistoricos {

    	EXPERIAN("EVENTO_EXPERIAN"),
    	INTEGRACION("EVENTO_SERVICIO_INTEGRACION"),
    	CUSTODIA("EVENTO_SERVICIO_CUSTODIA");

        private final String descripcion;

        TiposEventosHistoricos(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    
}