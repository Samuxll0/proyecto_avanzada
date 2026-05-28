package com.proyecto_avanzada.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.domain.enums.Rol;
import com.proyecto_avanzada.dto.AuthDTOs;
import com.proyecto_avanzada.dto.CatalogoDTOs;
import com.proyecto_avanzada.dto.GlobalDTOs;
import com.proyecto_avanzada.mapper.UsuarioMapper;
import com.proyecto_avanzada.repository.UsuarioRepository;
import com.proyecto_avanzada.security.JwtService;
import com.proyecto_avanzada.security.UserDetailsImpl;
import com.proyecto_avanzada.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            @Nullable UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        if (usuarioService != null) {
            this.usuarioService = usuarioService;
        } else {
            this.usuarioService = new UsuarioService(usuarioRepository);
        }
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public AuthDTOs.LoginResponse login(@RequestBody AuthDTOs.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", userDetails.getUsuario().getRol().name());
        extraClaims.put("nombre", userDetails.getUsuario().getNombre());
        String token = jwtService.generateToken(extraClaims, userDetails);
        return new AuthDTOs.LoginResponse(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalDTOs.SuccessResponse<Void> register(
            @jakarta.validation.Valid @RequestBody AuthDTOs.RegisterRequest request) {
        if (usuarioService.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El correo ya está registrado.");
        }

        Rol rolAsignado;
        String email = request.email().toLowerCase();

        if (email.endsWith("@estudiante.triagre.com")) {
            rolAsignado = Rol.ESTUDIANTE;
        } else if (email.endsWith("@docente.triagre.com")) {
            rolAsignado = Rol.DOCENTE;
        } else if (email.endsWith("@administrativo.triagre.com")) {
            rolAsignado = Rol.ADMINISTRATIVO;
        } else if (email.endsWith("@coordinacion.triagre.com")) {
            rolAsignado = Rol.COORDINADOR;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dominio de correo no autorizado. Use un correo institucional (ej. @estudiante.triagre.com).");
        }

        Usuario nuevoUsuario = usuarioMapper.toEntity(request);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(passwordEncoder.encode(request.password()));
        nuevoUsuario.setRol(rolAsignado);
        nuevoUsuario.setActivo(true);

        usuarioService.saveUsuario(nuevoUsuario);

        return new GlobalDTOs.SuccessResponse<>(
                "Usuario registrado con éxito. Por favor inicie sesión.", null);
    }

    // ── Perfil ────────────────────────────────────────────────
    @GetMapping("/perfil")
    public GlobalDTOs.SuccessResponse<CatalogoDTOs.UsuarioResponse> getPerfil(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return new GlobalDTOs.SuccessResponse<>("Perfil", usuarioMapper.toResponse(usuario));
    }

    @PutMapping("/perfil")
    public GlobalDTOs.SuccessResponse<CatalogoDTOs.UsuarioResponse> actualizarPerfil(
            @jakarta.validation.Valid @RequestBody AuthDTOs.CambiarPasswordRequest request,
            Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (request.nombre() != null && !request.nombre().isBlank()) {
            usuario.setNombre(request.nombre());
        }
        if (request.nuevaPassword() != null && !request.nuevaPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));
        }

        usuarioRepository.save(usuario);
        return new GlobalDTOs.SuccessResponse<>("Perfil actualizado", usuarioMapper.toResponse(usuario));
    }
}
