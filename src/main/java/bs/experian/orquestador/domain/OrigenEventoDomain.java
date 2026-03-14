package bs.experian.orquestador.domain;

import org.springframework.stereotype.Component;

import static bs.experian.orquestador.domain.enums.DomainEnum.TiposEventosHistoricos.*;

@Component
public class OrigenEventoDomain {
	public String origen(String notificationId) {
		String origen = EXPERIAN.name();

        if (notificationId != null) {
            String id = notificationId.toUpperCase();

            if (id.contains(INTEGRACION.name())) {
                origen = INTEGRACION.name();
            } else if (id.contains(CUSTODIA.name())) {
                origen = CUSTODIA.name();
            }
        }
		return origen;
	}

}
