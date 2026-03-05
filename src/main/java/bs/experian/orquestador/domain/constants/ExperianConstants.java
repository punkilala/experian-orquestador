package bs.experian.orquestador.domain.constants;


public final class ExperianConstants {

    private ExperianConstants() {
    	 throw new UnsupportedOperationException(ExperianConstants.class.getName() + " no instanciable");
    }

    public static final String EVENT_STATUS_CHANGED = "StatusChanged";
    public static final String EVENT_NEW_DOCUMENT_AVAILABLE = "NewDocumentAvailable";

    public static final String STATUS_SUCCESS = "Success";

    public static final String SUBSTATUS_ALL_DOCUMENTS_DOWNLOADED = "all_documents_downloaded";
    public static final String SUBSTATUS_PARTIAL_DOCUMENTS_DOWNLOADED = "partial_documents_downloaded";
    
    public static final String STATUS_PROCESSING = "Processing";
    public static final String SUBSTATUS_NEW_DOCUMENT_AVAILABLE = "new_document_available";
    
    public static final String STATUS_ERROR = "Error";
    public static final String SUBSTATUS_DOCUMENT_DWONLOAD_FAILED = "document_download_failed";
}