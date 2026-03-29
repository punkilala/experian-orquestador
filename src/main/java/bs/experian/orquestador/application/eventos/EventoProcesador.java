package bs.experian.orquestador.application.eventos;


import bs.experian.orquestador.application.model.evento.EventoDto;



public interface EventoProcesador {
	
	boolean aplica(EventoDto evento);

    void procesar(EventoDto evento);

}
