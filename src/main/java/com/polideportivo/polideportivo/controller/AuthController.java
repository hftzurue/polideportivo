package com.polideportivo.polideportivo.controller;

import com.polideportivo.polideportivo.dto.AuthResponse;
import com.polideportivo.polideportivo.dto.LoginRequest;
import com.polideportivo.polideportivo.entity.Usuario;
import com.polideportivo.polideportivo.enums.Rol;
import com.polideportivo.polideportivo.repository.UsuarioRepository;
import com.polideportivo.polideportivo.service.UsuarioService;
import com.polideportivo.polideportivo.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UsuarioService usuarioService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(request.getNombreUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        boolean contrasenaCorrecta = passwordEncoder.matches(
                request.getContrasena(),
                usuario.getContrasena()
        );

        if (!contrasenaCorrecta) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getRol().name()
        );

        return new AuthResponse(token);
    }

}