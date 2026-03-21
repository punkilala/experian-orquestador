package bs.experian.orquestador.application.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

public class OrquestadorUtils {
	/**
	 * Convertir un pila de errores a String y  devolviendo un numero de lineas determinadas
	 * @param e
	 * @param maxLineas
	 * @return
	 */
	public static String stackTraceToString(Throwable e, int maxLineas) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);

	    String[] lineas = sw.toString().split("\n");

	    StringBuilder resultado = new StringBuilder();
	    for (int i = 0; i < Math.min(maxLineas, lineas.length); i++) {
	        resultado.append(lineas[i]).append("\n");
	    }

	    return resultado.toString();
	}

	 private OrquestadorUtils() {
	   throw new IllegalAccessError("clase no instanciable");
	 }
	 
	 /**
	  * Obtener el ora de una excepcion de base datos
	  */
	 public static String getOra(Throwable ex) {		 
	     String ora = null;
	     Throwable cause = ex;
	     while (cause != null) {
	         if (cause instanceof SQLException sqlException) {	 
	             ora = String.format("ORA-%05d", sqlException.getErrorCode());
	             break;
	         }
	        cause = cause.getCause();
	     }
	     return ora;
	}
}
