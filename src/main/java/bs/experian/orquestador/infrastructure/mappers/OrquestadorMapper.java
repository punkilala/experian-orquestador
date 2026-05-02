package bs.experian.orquestador.infrastructure.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import bs.experian.orquestador.infrastructure.dto.orquestador.SolicitudActivaDto;
import bs.experian.orquestador.infrastructure.persistence.solicitud.SolicitudEntity;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrquestadorMapper {
	
	List<SolicitudActivaDto> entityListToDtoList(List<SolicitudEntity> entities);
	

}
