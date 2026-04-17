package com.proyecto_avanzada.service;

import com.proyecto_avanzada.domain.entity.HistorialSolicitud;
import com.proyecto_avanzada.dto.IADTOs;
import java.util.List;

public interface AIService {
    IADTOs.IAResponse sugerirClasificacion(IADTOs.IARequest request);

    IADTOs.ResumenResponse generarResumenHistorial(List<HistorialSolicitud> historial);
}
