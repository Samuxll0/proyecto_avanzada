package com.proyecto_avanzada.service;

import com.proyecto_avanzada.domain.entity.HistorialSolicitud;
import com.proyecto_avanzada.domain.entity.TipoSolicitud;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.dto.IADTOs;
import com.proyecto_avanzada.repository.TipoSolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MockAIService implements AIService {

    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final Random random = new Random();

    @Override
    public IADTOs.IAResponse sugerirClasificacion(IADTOs.IARequest request) {
        List<TipoSolicitud> tipos = tipoSolicitudRepository.findAll();

        TipoSolicitud tipoSugerido = null;
        if (!tipos.isEmpty()) {
            tipoSugerido = tipos.get(random.nextInt(tipos.size()));
        }

        NivelPrioridad[] prioridades = NivelPrioridad.values();
        NivelPrioridad prioridadSugerida = prioridades[random.nextInt(prioridades.length)];

        // Confianza aleatoria entre 0.50 y 0.99
        double confianza = 0.50 + (0.49 * random.nextDouble());
        confianza = Math.round(confianza * 100.0) / 100.0;

        return new IADTOs.IAResponse(
                tipoSugerido != null ? tipoSugerido.getId() : null,
                tipoSugerido != null ? tipoSugerido.getNombre() : "Desconocido",
                prioridadSugerida,
                confianza);
    }

    @Override
    public IADTOs.ResumenResponse generarResumenHistorial(List<HistorialSolicitud> historial) {
        if (historial == null || historial.isEmpty()) {
            return new IADTOs.ResumenResponse("La solicitud no posee un historial registrado.");
        }

        int cambios = historial.size();
        String estadoFinal = historial.get(0).getEstadoNuevo().name();

        String resumen = String.format(
                "Según el soporte de IA (Mock): Tras analizar la traza, la solicitud ha tenido %d actualizaciones en su ciclo de vida. Actualmente reposa en el estado %s. Las anotaciones indican manipulación de atención.",
                cambios, estadoFinal);

        return new IADTOs.ResumenResponse(resumen);
    }
}
