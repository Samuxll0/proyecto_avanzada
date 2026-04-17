package com.proyecto_avanzada.controller;

import com.proyecto_avanzada.domain.entity.Usuario;
import com.proyecto_avanzada.domain.enums.Rol;
import com.proyecto_avanzada.repository.UsuarioRepository;
import com.proyecto_avanzada.dto.GlobalDTOs;
import com.proyecto_avanzada.dto.AuthDTOs;
import com.proyecto_avanzada.security.JwtService;
import com.proyecto_avanzada.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthDTOs.LoginResponse login(@RequestBody AuthDTOs.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return new AuthDTOs.LoginResponse(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalDTOs.SuccessResponse<Void> register(
            @jakarta.validation.Valid @RequestBody AuthDTOs.RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
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

        Usuario nuevoUsuario = Usuario.builder()
                .nombre(request.nombre())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .rol(rolAsignado)
                .activo(true)
                .build();

        usuarioRepository.save(nuevoUsuario);

        return new GlobalDTOs.SuccessResponse<>(
                "Usuario registrado con éxito. Por favor inicie sesión.", null);
    }
}
