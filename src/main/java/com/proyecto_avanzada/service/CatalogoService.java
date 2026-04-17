package com.proyecto_avanzada.service;

import com.proyecto_avanzada.domain.entity.TipoSolicitud;
import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.dto.CatalogoDTOs;
import com.proyecto_avanzada.repository.TipoSolicitudRepository;
import com.proyecto_avanzada.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto_avanzada.domain.enums.NivelPrioridad;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final UsuarioRepository usuarioRepository;
    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final PasswordEncoder passwordEncoder;

    public List<CatalogoDTOs.UsuarioResponse> obtenerUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new CatalogoDTOs.UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CatalogoDTOs.TipoSolicitudResponse crearTipoSolicitud(CatalogoDTOs.TipoSolicitudRequest request) {
        TipoSolicitud tipo = TipoSolicitud.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .build();
        TipoSolicitud saved = tipoSolicitudRepository.save(tipo);
        return new CatalogoDTOs.TipoSolicitudResponse(saved.getId(), saved.getNombre(), saved.getDescripcion());
    }

    public List<CatalogoDTOs.TipoSolicitudResponse> obtenerTiposSolicitud() {
        return tipoSolicitudRepository.findAll().stream()
                .map(t -> new CatalogoDTOs.TipoSolicitudResponse(t.getId(), t.getNombre(), t.getDescripcion()))
                .collect(Collectors.toList());
    }

    public List<CatalogoDTOs.PrioridadResponse> obtenerPrioridades() {
        return java.util.Arrays.stream(NivelPrioridad.values())
                .map(p -> new CatalogoDTOs.PrioridadResponse(p.name()))
                .collect(Collectors.toList());
    }
}
