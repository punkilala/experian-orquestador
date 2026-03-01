package bs.experian.orquestador.utils;

public class OrquestadorUtils {
	public static String extraerCodigoOracle(Throwable e) {
		String result = null;
		while (e != null) {
	        if (e instanceof java.sql.SQLException sqlEx) {
	            int codigo = sqlEx.getErrorCode();
	            if (codigo > 0) {
	                result = "ORA-" + String.format("%05d", codigo);
	            }
	        }
	        e = e.getCause();
	    }
	    return result;
	}	
}
