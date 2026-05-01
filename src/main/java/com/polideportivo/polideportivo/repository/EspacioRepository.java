package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Integer> {
    Optional<Espacio> findByNombreIgnoreCase(String nombre);
    List<Espacio> findByNombreContainingIgnoreCase(String nombre);
    List<Espacio> findByActivoTrue();
    List<Espacio> findByActivoFalse();
    // Buscar espacios activos por disciplina
    List<Espacio> findByDisciplina_IdDisciplinaAndActivoTrue(Integer idDisciplina);
    // Buscar espacios por disciplina, sin importar si estan activos o no
    List<Espacio> findByDisciplina_IdDisciplina(Integer idDisciplina);
    // Buscar espacios activos con capacidad mayor o igual a la solicitada
    List<Espacio> findByCapacidadGreaterThanEqualAndActivoTrue(Integer capacidad);
    // Buscar espacios activos que estén abiertos dentro de un horario solicitado
    List<Espacio> findByActivoTrueAndHoraAperturaLessThanEqualAndHoraCierreGreaterThanEqual(
            LocalTime horaInicio,
            LocalTime horaFin
    );
    // Buscar espacios activos por disciplina que estén abiertos dentro de un horario solicitado
    List<Espacio> findByDisciplina_IdDisciplinaAndActivoTrueAndHoraAperturaLessThanEqualAndHoraCierreGreaterThanEqual(
            Integer idDisciplina,
            LocalTime horaInicio,
            LocalTime horaFin
    );
    boolean existsByNombreIgnoreCase(String nombre);
}
