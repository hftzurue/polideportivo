package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Usuario;
import com.polideportivo.polideportivo.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}
