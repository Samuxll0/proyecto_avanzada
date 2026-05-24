package com.proyecto_avanzada.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto_avanzada.domain.entity.HistorialSolicitud;
import com.proyecto_avanzada.domain.entity.TipoSolicitud;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;
import com.proyecto_avanzada.dto.IADTOs;
import com.proyecto_avanzada.repository.TipoSolicitudRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockAIService implements AIService {

    private final TipoSolicitudRepository tipoSolicitudRepository;


    @Override
    public IADTOs.IAResponse sugerirClasificacion(IADTOs.IARequest request) {
        String texto = request.descripcion().toLowerCase();
        List<TipoSolicitud> tipos = tipoSolicitudRepository.findAll();

        // Puntuar tipos por coincidencia de palabras clave
        TipoSolicitud mejor = null;
        int mejorPuntaje = 0;
        for (TipoSolicitud t : tipos) {
            int puntaje = 0;
            String nombre = t.getNombre() != null ? t.getNombre().toLowerCase() : "";
            String desc = t.getDescripcion() != null ? t.getDescripcion().toLowerCase() : "";

            for (String palabra : splitWords(nombre)) {
                if (!palabra.isBlank() && texto.contains(palabra)) puntaje += 3;
            }
            for (String palabra : splitWords(desc)) {
                if (!palabra.isBlank() && texto.contains(palabra)) puntaje += 1;
            }

            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = t;
            }
        }

        // Determinar prioridad por heurísticas simples
        NivelPrioridad prioridad = heuristicaPrioridad(texto);

        double confianza;
        if (mejor != null && mejorPuntaje > 0) {
            // confianza basada en puntaje relativo
            confianza = Math.min(0.95, 0.5 + (mejorPuntaje / 20.0));
        } else {
            confianza = 0.55; // baja confianza si no se detectó tipo
        }
        confianza = Math.round(confianza * 100.0) / 100.0;

        return new IADTOs.IAResponse(
                mejor != null ? mejor.getId() : null,
                mejor != null ? mejor.getNombre() : "No detectado",
                prioridad,
                confianza);
    }

    @Override
    public IADTOs.ResumenResponse generarResumenHistorial(List<HistorialSolicitud> historial) {
        if (historial == null || historial.isEmpty()) {
            return new IADTOs.ResumenResponse("La solicitud no posee un historial registrado.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Resumen generado por IA:\n");
        sb.append(String.format("- Cambios registrados: %d\n", historial.size()));

        HistorialSolicitud ultimo = historial.get(0);
        sb.append(String.format("- Estado actual: %s (último cambio: %s)\n", ultimo.getEstadoNuevo().name(),
                ultimo.getFechaCambio()));

        // Agregar últimas anotaciones (hasta 5)
        sb.append("- Últimas anotaciones:\n");
        int limit = Math.min(5, historial.size());
        for (int i = 0; i < limit; i++) {
            HistorialSolicitud h = historial.get(i);
            String autor = h.getAutorCambio() != null ? h.getAutorCambio().getEmail() : "Sistema";
            sb.append(String.format("  %d) [%s] %s - %s\n", i + 1, h.getFechaCambio(), h.getEstadoNuevo(),
                    firstN(h.getComentarios(), 160)));
        }

        // Detección simple de patrones: cambios frecuentes entre estados indican re-asignaciones
        boolean reasignaciones = historial.stream().anyMatch(h -> h.getComentarios() != null && h.getComentarios().toLowerCase().contains("asign"));
        if (reasignaciones) {
            sb.append("- Observación: se detectaron reasignaciones o cambios en el responsable.\n");
        }

        String resumen = sb.toString();
        return new IADTOs.ResumenResponse(resumen);
    }

    private static String[] splitWords(String s) {
        return s.split("\\W+");
    }

    private static NivelPrioridad heuristicaPrioridad(String texto) {
        if (texto.contains("urgente") || texto.contains("inmedi") || texto.contains("examen") || texto.contains("nota") || texto.contains("critico") || texto.contains("crítico")) {
            return NivelPrioridad.ALTA;
        }
        if (texto.contains("próxima") || texto.contains("semana") || texto.contains("siguiente") || texto.contains("prox")) {
            return NivelPrioridad.MEDIA;
        }
        return NivelPrioridad.BAJA;
    }

    private static String firstN(String s, int n) {
        if (s == null) return "";
        return s.length() <= n ? s : s.substring(0, n) + "...";
    }
}
