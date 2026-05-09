package bs.experian.orquestador.infrastructure.kafka.avro;

import bs.experian.orquestador.application.model.evento.EventoDto;

public interface TiposEventosProcesador {
	boolean soportado(Object evento);
    EventoDto procesar(Object evento);
}
