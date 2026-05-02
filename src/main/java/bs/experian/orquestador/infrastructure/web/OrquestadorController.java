package bs.experian.orquestador.infrastructure.web;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.orquestador.application.SolicitudApplicationService;
import bs.experian.orquestador.infrastructure.dto.integracion.SolicitudNuevaRequest;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudNuevaResponse;
import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudesActivasResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/experian")
@RequiredArgsConstructor
public class OrquestadorController {

	private final SolicitudApplicationService solicitudApplicationService;

	
	@PostMapping("/solicitudes")
	public ResponseEntity<SolicitudNuevaResponse> nuevaSolicitud(@Valid @RequestBody SolicitudNuevaRequest solicitudRequestDto){
		return ResponseEntity.ok(solicitudApplicationService.crearSolicitud(solicitudRequestDto));
		
	}
	
	@GetMapping("/solicitudes/activas/{idFiscal}")
	public ResponseEntity<SolicitudesActivasResponse>recepcionEventos(@PathVariable String idFiscal){
		return ResponseEntity.ok(solicitudApplicationService.obtenerSolicitudesActivas(idFiscal));
	}

}
