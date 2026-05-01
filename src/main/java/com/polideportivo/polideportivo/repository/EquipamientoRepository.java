package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Equipamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamientoRepository extends JpaRepository<Equipamiento, Integer> {
    Optional<Equipamiento> findByNombreIgnoreCase(String nombre);
    List<Equipamiento> findByNombreContainingIgnoreCase(String nombre);
    List<Equipamiento> findByActivoTrue();
    List<Equipamiento> findByActivoFalse();
    // Buscar equipamientos activos por disciplina
    List<Equipamiento> findByDisciplina_IdDisciplinaAndActivoTrue(Integer idDisciplina);
    // Buscar equipamientos por disciplina, sin importar si estan activos o no
    List<Equipamiento> findByDisciplina_IdDisciplina(Integer idDisciplina);
    boolean existsByNombreIgnoreCase(String nombre);
}
