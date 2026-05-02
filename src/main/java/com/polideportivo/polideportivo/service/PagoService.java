package com.polideportivo.polideportivo.service;

import org.springframework.stereotype.Service;
import com.polideportivo.polideportivo.entity.Pago;
import com.polideportivo.polideportivo.entity.Reserva;
import com.polideportivo.polideportivo.enums.EstadoPago;
import com.polideportivo.polideportivo.enums.EstadoReserva;
import com.polideportivo.polideportivo.enums.MetodoPago;
import com.polideportivo.polideportivo.repository.PagoRepository;
import com.polideportivo.polideportivo.repository.ReservaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public PagoService(PagoRepository pagoRepository, ReservaRepository reservaRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    public Pago crearPago(Pago pago) {
        if (pago.getReserva() == null || pago.getReserva().getIdReserva() == null) {
            throw new RuntimeException("La reserva es obligatoria");
        }

        if (pago.getMetodoPago() == null) {
            throw new RuntimeException("El método de pago es obligatorio");
        }

        Integer idReserva = pago.getReserva().getIdReserva();

        if (pagoRepository.existsByReserva_IdReserva(idReserva)) {
            throw new RuntimeException("La reserva ya tiene un pago registrado");
        }

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se puede pagar una reserva cancelada");
        }

        BigDecimal montoFinal = calcularMontoReserva(reserva);

        pago.setReserva(reserva);
        pago.setMonto(montoFinal);

        if (pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDateTime.now());
        }

        if (pago.getEstadoPago() == null) {
            pago.setEstadoPago(EstadoPago.PENDIENTE);
        }

        Pago pagoGuardado = pagoRepository.save(pago);

        if (pagoGuardado.getEstadoPago() == EstadoPago.APROBADO) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva.setMontoTotal(montoFinal);
            reservaRepository.save(reserva);
        }

        return pagoGuardado;
    }

    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    public Pago obtenerPorId(Integer id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    public Pago obtenerPorReserva(Integer idReserva) {
        return pagoRepository.findByReserva_IdReserva(idReserva)
                .orElseThrow(() -> new RuntimeException("No hay pago registrado para esa reserva"));
    }

    public List<Pago> obtenerPorEstado(EstadoPago estadoPago) {
        return pagoRepository.findByEstadoPago(estadoPago);
    }

    public List<Pago> obtenerPorMetodo(MetodoPago metodoPago) {
        return pagoRepository.findByMetodoPago(metodoPago);
    }

    public List<Pago> obtenerPorEstadoYMetodo(EstadoPago estadoPago, MetodoPago metodoPago) {
        return pagoRepository.findByEstadoPagoAndMetodoPago(estadoPago, metodoPago);
    }

    public List<Pago> obtenerEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("Rango de fechas inválido");
        }

        return pagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(fechaInicio, fechaFin);
    }

    public List<Pago> obtenerPorUsuario(Integer idUsuario) {
        return pagoRepository.findByReserva_Usuario_IdUsuario(idUsuario);
    }

    public List<Pago> obtenerPorEspacio(Integer idEspacio) {
        return pagoRepository.findByReserva_Espacio_IdEspacio(idEspacio);
    }

    public List<Pago> obtenerPorFechaReserva(LocalDate fechaReserva) {
        return pagoRepository.findByReserva_FechaReserva(fechaReserva);
    }

    public Pago actualizarEstadoPago(Integer idPago, EstadoPago estadoPago) {
        if (estadoPago == null) {
            throw new RuntimeException("El estado del pago es obligatorio");
        }

        Pago pago = obtenerPorId(idPago);
        pago.setEstadoPago(estadoPago);

        Reserva reserva = pago.getReserva();

        if (estadoPago == EstadoPago.APROBADO) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reservaRepository.save(reserva);
        }

        return pagoRepository.save(pago);
    }

    public void eliminarPago(Integer id) {
        Pago pago = obtenerPorId(id);
        pagoRepository.delete(pago);
    }

    private void validarDatosPago(Pago pago) {
        if (pago.getReserva() == null || pago.getReserva().getIdReserva() == null) {
            throw new RuntimeException("La reserva es obligatoria");
        }

        if (pago.getMonto() == null || pago.getMonto().signum() <= 0) {
            throw new RuntimeException("El monto debe ser mayor a 0");
        }

        if (pago.getMetodoPago() == null) {
            throw new RuntimeException("El método de pago es obligatorio");
        }
    }

    private BigDecimal calcularMontoReserva(Reserva reserva) {
        long minutos = java.time.Duration.between(
                reserva.getHoraInicio(),
                reserva.getHoraFin()
        ).toMinutes();

        if (minutos <= 0) {
            throw new RuntimeException("El horario de la reserva no es válido");
        }

        BigDecimal horas = BigDecimal.valueOf(minutos)
                .divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);

        return reserva.getEspacio().getPrecioHora().multiply(horas);
    }
}