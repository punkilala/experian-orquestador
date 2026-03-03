package bs.experian.orquestador.worker.eventos;

import bs.experian.orquestador.dto.EventoProcesadoDto;
import bs.experian.orquestador.entity.EventoExperianVivoEntity;

public interface EventoProcesador {
	
	boolean aplica(EventoProcesadoDto evento);

    EventoProcesadoDto procesar(EventoProcesadoDto evento);

}
