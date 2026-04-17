package com.proyecto_avanzada.controller;

import com.proyecto_avanzada.dto.IADTOs;
import com.proyecto_avanzada.service.AIService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ia")
public class IAController {

    private final AIService aiService;

    // Se inyecta la implementación (que actualmente es Mock)
    public IAController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/sugerencia-clasificacion")
    public IADTOs.IAResponse sugerir(@Valid @RequestBody IADTOs.IARequest request) {
        return aiService.sugerirClasificacion(request);
    }
}
