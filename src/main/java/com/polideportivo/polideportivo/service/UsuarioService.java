package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Usuario;
import com.polideportivo.polideportivo.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UsuarioService{

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        usuarioRepository.findByCorreoIgnoreCase(usuario.getCorreo())
                .ifPresent(u -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,"El correo ya está registrado");
                });

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
        existente.setRol(usuario.getRol());

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
        if (usuario.getCorreo() != null) {
            existente.setCorreo(usuario.getCorreo());
        }
        if (usuario.getRol() != null) {
            existente.setRol(usuario.getRol());
        }

        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Integer id) {
        Usuario usuario = obtenerPorId(id);
        usuarioRepository.delete(usuario);
    }
}
