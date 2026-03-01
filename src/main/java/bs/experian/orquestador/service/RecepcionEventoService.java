package bs.experian.orquestador.service;

import bs.experian.orquestador.dto.EventoDto;

public interface RecepcionEventoService {
	void recibirEventoExperian (EventoDto request);
}
