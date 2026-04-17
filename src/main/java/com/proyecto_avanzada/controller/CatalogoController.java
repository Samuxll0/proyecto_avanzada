package com.proyecto_avanzada.controller;

import com.proyecto_avanzada.dto.CatalogoDTOs;
import com.proyecto_avanzada.dto.GlobalDTOs;
import com.proyecto_avanzada.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('COORDINADOR')")
    public List<CatalogoDTOs.UsuarioResponse> getUsuarios() {
        return catalogoService.obtenerUsuarios();
    }

    @PostMapping("/tipos-solicitud")
    @PreAuthorize("hasRole('COORDINADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalDTOs.SuccessResponse<CatalogoDTOs.TipoSolicitudResponse> crearTipoSolicitud(
            @Valid @RequestBody CatalogoDTOs.TipoSolicitudRequest request) {
        return new GlobalDTOs.SuccessResponse<>("Tipo Solicitud creado", catalogoService.crearTipoSolicitud(request));
    }

    @GetMapping("/tipos-solicitud")
    public List<CatalogoDTOs.TipoSolicitudResponse> getTiposSolicitud() {
        return catalogoService.obtenerTiposSolicitud();
    }

    @GetMapping("/prioridades")
    public List<CatalogoDTOs.PrioridadResponse> getPrioridades() {
        return catalogoService.obtenerPrioridades();
    }
}
