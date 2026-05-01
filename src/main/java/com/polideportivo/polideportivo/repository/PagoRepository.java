package com.polideportivo.polideportivo.repository;

import com.polideportivo.polideportivo.entity.Pago;
import com.polideportivo.polideportivo.enums.EstadoPago;
import com.polideportivo.polideportivo.enums.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Optional<Pago> findByReserva_IdReserva(Integer idReserva);
    // Validar si una reserva ya tiene pago registrado
    boolean existsByReserva_IdReserva(Integer idReserva);
    List<Pago> findByEstadoPago(EstadoPago estadoPago);
    List<Pago> findByMetodoPago(MetodoPago metodoPago);
    // Buscar pagos por estado y método
    List<Pago> findByEstadoPagoAndMetodoPago(
            EstadoPago estadoPago,
            MetodoPago metodoPago
    );
    // Buscar pagos realizados entre dos fechas
    List<Pago> findByFechaPagoBetweenOrderByFechaPagoDesc(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    // Buscar pagos de un usuario por medio de la reserva
    List<Pago> findByReserva_Usuario_IdUsuario(Integer idUsuario);
    // Buscar pagos de un espacio por medio de la reserva
    List<Pago> findByReserva_Espacio_IdEspacio(Integer idEspacio);
    // Buscar pagos de una fecha específica de reserva
    List<Pago> findByReserva_FechaReserva(LocalDate fechaReserva);
}
