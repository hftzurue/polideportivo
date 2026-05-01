package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
