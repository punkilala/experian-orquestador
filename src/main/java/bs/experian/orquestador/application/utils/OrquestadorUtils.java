package bs.experian.orquestador.application.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class OrquestadorUtils {

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
}
