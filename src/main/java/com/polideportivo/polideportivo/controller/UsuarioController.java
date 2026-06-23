package com.polideportivo.polideportivo.controller;

import com.polideportivo.polideportivo.entity.Usuario;
import com.polideportivo.polideportivo.enums.Rol;
import com.polideportivo.polideportivo.security.SecurityUtils;
import com.polideportivo.polideportivo.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Registro público
    @PostMapping
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        usuario.setRol(Rol.CLIENTE); // evita que cualquiera se registre como admin
        return usuarioService.crearUsuario(usuario);
    }

    // Perfil propio
    @GetMapping("/me")
    public Usuario obtenerMiPerfil() {
        Integer idUsuario = SecurityUtils.obtenerIdUsuarioAutenticado();
        return usuarioService.obtenerPorId(idUsuario);
    }

    @PatchMapping("/me")
    public Usuario actualizarMiPerfil(@RequestBody Usuario usuario) {
        Integer idUsuario = SecurityUtils.obtenerIdUsuarioAutenticado();

        usuario.setRol(null);
        usuario.setContrasena(null);

        return usuarioService.actualizarUsuarioParcial(idUsuario, usuario);
    }

    // Admin
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Usuario obtenerPorId(@PathVariable Integer id) {
        return usuarioService.obtenerPorId(id);
    }

    @GetMapping("/correo/{correo}")
    public Usuario obtenerPorCorreo(@PathVariable String correo) {
        return usuarioService.obtenerPorCorreo(correo);
    }

    @GetMapping("/buscar")
    public List<Usuario> buscarPorNombre(@RequestParam String nombre) {
        return usuarioService.buscarPorNombre(nombre);
    }

    @PutMapping("/{id}")
    public Usuario actualizarUsuarioCompleto(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuarioCompleto(id, usuario);
    }

    @PatchMapping("/{id}")
    public Usuario actualizarUsuarioParcial(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuarioParcial(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
    }
}