package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Reserva;
import com.polideportivo.polideportivo.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByUsuario_IdUsuario(Integer idUsuario);
    List<Reserva> findByUsuario_IdUsuarioOrderByFechaReservaAscHoraInicioAsc(Integer idUsuario);
    List<Reserva> findByEstado(EstadoReserva estado);
    // Consultar reservas activas de un usuario
    List<Reserva> findByUsuario_IdUsuarioAndEstadoInOrderByFechaReservaAscHoraInicioAsc(
            Integer idUsuario,
            Collection<EstadoReserva> estados
    );
    // Consultar reservas activas generales
    List<Reserva> findByEstadoInOrderByFechaReservaAscHoraInicioAsc(
            Collection<EstadoReserva> estados
    );
    // Buscar reservas por espacio
    List<Reserva> findByEspacio_IdEspacio(Integer idEspacio);
    // Buscar reservas de un espacio en una fecha específica
    List<Reserva> findByEspacio_IdEspacioAndFechaReservaOrderByHoraInicioAsc(
            Integer idEspacio,
            LocalDate fechaReserva
    );
    // Buscar reservas de un espacio en una fecha y con ciertos estados
    List<Reserva> findByEspacio_IdEspacioAndFechaReservaAndEstadoInOrderByHoraInicioAsc(
            Integer idEspacio,
            LocalDate fechaReserva,
            Collection<EstadoReserva> estados
    );
    // Buscar una reserva específica de un usuario
    Optional<Reserva> findByIdReservaAndUsuario_IdUsuario(
            Integer idReserva,
            Integer idUsuario
    );
    // Buscar reservas entre fechas
    List<Reserva> findByFechaReservaBetweenOrderByFechaReservaAscHoraInicioAsc(
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
    // Validar choque de horario para registrar reserva
    @Query("""
           SELECT COUNT(r)
           FROM Reserva r
           WHERE r.espacio.idEspacio = :idEspacio
           AND r.fechaReserva = :fechaReserva
           AND r.estado IN :estados
           AND r.horaInicio < :horaFin
           AND r.horaFin > :horaInicio
           """)
    Long contarReservasEnConflicto(
            Integer idEspacio,
            LocalDate fechaReserva,
            LocalTime horaInicio,
            LocalTime horaFin,
            Collection<EstadoReserva> estados
    );
    // Obtener reservas futuras de un usuario
    List<Reserva> findByUsuario_IdUsuarioAndFechaReservaGreaterThanEqualOrderByFechaReservaAscHoraInicioAsc(
            Integer idUsuario,
            LocalDate fechaActual
    );
    // Obtener reservas por fecha de creacion
    List<Reserva> findByFechaCreacionBetween(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}

