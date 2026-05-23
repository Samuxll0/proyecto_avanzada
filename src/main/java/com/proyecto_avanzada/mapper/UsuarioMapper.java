package com.proyecto_avanzada.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.dto.AuthDTOs;
import com.proyecto_avanzada.dto.CatalogoDTOs;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    CatalogoDTOs.UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Usuario toEntity(AuthDTOs.RegisterRequest request);

}
