package bs.experian.orquestador.infrastructure.web;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.orquestador.application.EventoApplicationService;
import bs.experian.orquestador.application.SolicitudApplicationService;
import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/experian")
@RequiredArgsConstructor
public class OrquestadorController {
	
	private final EventoApplicationService eventoApplicationService;
	private final SolicitudApplicationService solicitudApplicationService;

	
	@PostMapping("/solicitudes")
	public ResponseEntity<SolicitudNuevaResponse> nuevaSolicitud(@Valid @RequestBody SolicitudNuevaRequest solicitudRequestDto){
		return ResponseEntity.ok(solicitudApplicationService.crearSolicitud(solicitudRequestDto));
		
	}
	
	@PostMapping("/eventos")
	public ResponseEntity<Void>recepcionEventos (@RequestBody EventoDto request){
		eventoApplicationService.recibirEventoExperian(request);
		return ResponseEntity.ok().build();
	}

}
