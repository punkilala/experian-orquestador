package bs.experian.orquestador.application.eventos;

import com.fasterxml.jackson.core.JsonProcessingException;

import bs.experian.orquestador.application.model.evento.EventoProcesadoDto;

public interface EventoProcesador {
	
	boolean aplica(EventoProcesadoDto evento);

    void procesar(EventoProcesadoDto evento) throws JsonProcessingException;

}
