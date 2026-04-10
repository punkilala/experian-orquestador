package bs.experian.orquestador.domain.constants;

import java.util.Set;

import bs.experian.orquestador.domain.enums.DomainEnum;

public final class ExperianConstants {

    private ExperianConstants() {
    	 throw new UnsupportedOperationException(ExperianConstants.class.getName() + " no instanciable");
    }
    
    //err ORA
    public static final String ORA_NO_EXISTE = "ORA-02291";
    public static final String ORA_DUPLICADO = "ORA-00001";
    
    //varios
    public static final String ERROR_PROCESAMIENTO = "ERROR_PROCESAMIENTO";
    public static final String UNKNOWN = "UNKNOWN";
    
    //eventos
    public static final String EVENT_STATUS_CHANGED = "StatusChanged";
    public static final String EVENT_NEW_DOCUMENT_AVAILABLE = "NewDocumentAvailable";

    public static final String STATUS_SUCCESS = "Success";
    
    public static final String STATUS_CANCELED = "Canceled";
    public static final String SUBSTATUS_CANCELED = "canceled";
    

    public static final String SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED = "all_documents_downloaded";
    public static final String SUBSTATUS_PARTIAL_DOCUMENTS_DOWNLOADED = "partial_documents_downloaded";
    
    public static final String STATUS_PROCESSING = "Processing";
    public static final String SUBSTATUS_NEW_DOCUMENT_AVAILABLE = "new_document_available";
    public static final String SUBSTATUS_CONSENT_PROCECCS_COMPLETED = "consent_process_completed";
    
    public static final String STATUS_ERROR = "Error";
    public static final String SUBSTATUS_NO_CLAVE_PIN_SERVICE = "no_clave_pin_service";
    public static final String SUBSTATUS_NO_SMS_SERVICE = "no_sms_service";
    public static final String SUBSTATUS_FAILED_DOCUMENTS_DOWNLOADED = "failed_documents_downloaded";
    public static final String SUBSTATUS_INVALID_LEGAL_REPRESENTATIVE = "invalid_legal_representative";
    public static final String SUBSTATUS_REJECTED_RMC_VALIDATION = "rejected_rmc_validation";
    
    public static final String SUBSTATUS_NINGUNO = "Ninguno";
    public static final String SUBSTATUS_CREATED = "created";
    public static final String SUBSTATUS_INVALID_IDENTIFYING = "invalid_identifying";
    public static final String SUBSTATUS_REJECTED_IDENTITY = "rejected_identity";
    public static final String SUBSTATUS_ABANDONED_AFTER_WAITING_OTP_AEAT = "abandoned_after_waiting_otp_aeat ";
    public static final String SUBSTATUS_ABANDONED_AFTER_INVALID_OTP_AEAT = "abandoned_after_invalid_otp_aeat";
    
    public static final String SUBSTATUS_ABANDONED_AFTER_WAIING_OTP_SEGURIDAD_SOCIAL = "abandoned_after_waiting_otp_seguridad_social";
    public static final String SUBSTATUS_ABANDONED_AFTER_INVALID_OTP_SEGURIDAD_SOCIAL = "abandoned_after_invalid_otp_seguridad_social";
    
    
    public static final String EVENT_DOCUMENTO_DESCARGADO = "DocumentoDescargado";
    public static final String STATUS_DOCUMENTO_DESCARGADO = "documento_descargado";
    
    public static final String DOC_NO_DESCARGADO = "NO_DESCARGADO";
    public static final String DOC_PTE_DESCARGA = "PTE_DESCARGA";
    public static final String DOC_PTE_CUSTODIA = "PTE_CUSTODIA";
    public static final String DOC_CUSTODIA_OK = "CUSTODIA_OK";
    public static final String DOC_CUSTODIA_KO = "CUSTODIA_KO";
    
  
    public static final String EVENT_TYPE_CUSTODIA = "CustodiaDocumento";
    public static final String STATUS_CUSTODIA = "custodia_documento";
    
    
    //listas
    public static final Set<DomainEnum.EstadoInterno> ESTADOS_EXPERIAN_CANCELADA_ERROR = Set.of(
	    		DomainEnum.EstadoInterno.CANCELADA,
	    	    DomainEnum.EstadoInterno.ERROR
    		);
    public static final Set<String> STATUS_FINALES_EXPERIAN = Set.of(
    		STATUS_SUCCESS,
    		STATUS_CANCELED,
    		STATUS_ERROR
		);
    
    
}