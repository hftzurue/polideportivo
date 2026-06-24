package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Usuario;
import com.polideportivo.polideportivo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UsuarioService{

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo");
        }

        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(usuario.getNombreUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe ese nombre de usuario");
        }

        usuario.setContrasena(
                passwordEncoder.encode(usuario.getContrasena())
        );

        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no encontrado"));
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario actualizarUsuarioCompleto(Integer id, Usuario usuario) {
        Usuario existente = obtenerPorId(id);

        existente.setNombre(usuario.getNombre());
        existente.setPrimerApellido(usuario.getPrimerApellido());
        existente.setSegundoApellido(usuario.getSegundoApellido());
        existente.setCorreo(usuario.getCorreo());

        return usuarioRepository.save(existente);
    }

    public Usuario actualizarUsuarioParcial(Integer id, Usuario usuario) {
        Usuario existente = obtenerPorId(id);

        if (usuario.getNombre() != null) {
            existente.setNombre(usuario.getNombre());
        }
        if (usuario.getPrimerApellido() != null) {
            existente.setPrimerApellido(usuario.getPrimerApellido());
        }
        if (usuario.getSegundoApellido() != null) {
            existente.setSegundoApellido(usuario.getSegundoApellido());
        }
        if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo()) && !usuario.getCorreo().equals(existente.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con ese correo");
        }
        if (usuario.getCorreo() != null) {
            existente.setCorreo(usuario.getCorreo());
        }

        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Integer id) {
        Usuario usuario = obtenerPorId(id);
        usuarioRepository.delete(usuario);
    }
}
