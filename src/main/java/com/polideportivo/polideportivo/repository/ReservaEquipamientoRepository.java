package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.ReservaEquipamiento;
import com.polideportivo.polideportivo.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservaEquipamientoRepository extends JpaRepository<ReservaEquipamiento, Integer> {
    List<ReservaEquipamiento> findByReserva_IdReserva(Integer idReserva);
    // Busca todas las reservas donde se solicitó un equipamiento específico
    List<ReservaEquipamiento> findByEquipamiento_IdEquipamiento(Integer idEquipamiento);
    // Busca si una reserva especifica ya tiene asociado un equipamiento especifico
    Optional<ReservaEquipamiento> findByReserva_IdReservaAndEquipamiento_IdEquipamiento(
            Integer idReserva,
            Integer idEquipamiento
    );
    // Valida si una reserva ya tiene asociado cierto equipamiento
    boolean existsByReserva_IdReservaAndEquipamiento_IdEquipamiento(
            Integer idReserva,
            Integer idEquipamiento
    );
    // Elimina los equipamiento asociado a una reserva
    void deleteByReserva_IdReserva(Integer idReserva);
    // Elimina un equipamiento específico dentro de una reserva específica
    void deleteByReserva_IdReservaAndEquipamiento_IdEquipamiento(
            Integer idReserva,
            Integer idEquipamiento
    );
    // Suma la cantidad total solicitada de un equipamiento específico, sin tomar en cuenta fecha, hora o estado de la reserva
    @Query("""
           SELECT COALESCE(SUM(re.cantidad), 0)
           FROM ReservaEquipamiento re
           WHERE re.equipamiento.idEquipamiento = :idEquipamiento
           """)
    Integer sumarCantidadSolicitadaPorEquipamiento(Integer idEquipamiento);
}
