package bs.experian.orquestador.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.orquestador.dto.EventoDto;
import bs.experian.orquestador.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.service.evento.RecepcionEventoService;
import bs.experian.orquestador.service.integracion.NuevaSolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/experian")
@RequiredArgsConstructor
public class OrquestadorController {
	
	private final NuevaSolicitudService solicitudService;
	private final RecepcionEventoService recepcionEventoService;
	
	@PostMapping("/solicitudes")
	public ResponseEntity<SolicitudNuevaResponse> nuevaSolicitud(@Valid @RequestBody SolicitudNuevaRequest solicitudResuestDto){
		return ResponseEntity.ok(solicitudService.crearSolicitud(solicitudResuestDto) );
		
	}
	
	@PostMapping("/eventos")
	public ResponseEntity<Void>recepcionEventos (@RequestBody EventoDto request){
		recepcionEventoService.recibirEventoExperian(request);
		return ResponseEntity.ok().build();
	}

}
