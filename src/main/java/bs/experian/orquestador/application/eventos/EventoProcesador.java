package bs.experian.orquestador.application.eventos;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;
import bs.experian.orquestador.infrastructure.persistence.eventos.EventoExperianVivoEntity;

public interface EventoProcesador {
	
	boolean aplica(EventoProcesadoDto evento);

    void procesar(EventoProcesadoDto evento);

}
