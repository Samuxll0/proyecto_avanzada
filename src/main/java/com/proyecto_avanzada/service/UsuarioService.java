package com.proyecto_avanzada.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario saveUsuario(Usuario u) {
        return usuarioRepository.save(u);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
}
