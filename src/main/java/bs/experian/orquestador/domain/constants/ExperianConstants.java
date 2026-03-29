package bs.experian.orquestador.domain.constants;


public final class ExperianConstants {

    private ExperianConstants() {
    	 throw new UnsupportedOperationException(ExperianConstants.class.getName() + " no instanciable");
    }
    
    //err ORA
    public static final String ORA_NO_EXISTE = "ORA-02291";
    public static final String ORA_DUPLICADO = "ORA-00001";
    
    //eventos
    public static final String EVENT_STATUS_CHANGED = "StatusChanged";
    public static final String EVENT_NEW_DOCUMENT_AVAILABLE = "NewDocumentAvailable";

    public static final String STATUS_SUCCESS = "Success";

    public static final String SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED = "all_documents_downloaded";
    public static final String SUBSTATUS_PARTIAL_DOCUMENTS_DOWNLOADED = "partial_documents_downloaded";
    
    public static final String STATUS_PROCESSING = "Processing";
    public static final String SUBSTATUS_NEW_DOCUMENT_AVAILABLE = "new_document_available";
    
    public static final String STATUS_ERROR = "Error";
    public static final String SUBSTATUS_DOCUMENT_DWONLOAD_FAILED = "document_download_failed";
    
    public static final String EVENT_DOCUMENTO_DESCARGADO = "DocumentoDescargado";
    public static final String STATUS_DOCUMENTO_DESCARGADO = "documento_descargado";
    
    public static final String DOC_NO_DESCARGADO = "NO_DESCARGADO";
    public static final String DOC_PTE_DESCARGA = "PTE_DESCARGA";
    public static final String DOC_PTE_CUSTODIA = "PTE_CUSTODIA";
    
    public static final String EVENT_TYPE_CUSTODIA = "CustodiaDocumento";
    public static final String STATUS_CUSTODIA = "custodia_documento";
    
    
    
}