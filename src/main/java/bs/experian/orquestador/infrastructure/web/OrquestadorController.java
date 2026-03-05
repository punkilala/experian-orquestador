package bs.experian.orquestador.infrastructure.web;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.orquestador.application.model.evento.EventoDto;
import bs.experian.orquestador.domain.services.EventoService;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.webclient.NuevaSolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/experian")
@RequiredArgsConstructor
public class OrquestadorController {
	
	private final NuevaSolicitudService solicitudService;
	private final EventoService eventoService;
	
	@PostMapping("/solicitudes")
	public ResponseEntity<SolicitudNuevaResponse> nuevaSolicitud(@Valid @RequestBody SolicitudNuevaRequest solicitudResuestDto){
		return ResponseEntity.ok(solicitudService.crearSolicitud(solicitudResuestDto) );
		
	}
	
	@PostMapping("/eventos")
	public ResponseEntity<Void>recepcionEventos (@RequestBody EventoDto request){
		eventoService.recibirEventoExperian(request);
		return ResponseEntity.ok().build();
	}

}
