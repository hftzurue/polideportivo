package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
    Optional<Disciplina> findByNombreIgnoreCase(String nombre);
    List<Disciplina> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
