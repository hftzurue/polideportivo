package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Equipamiento;
import com.polideportivo.polideportivo.entity.Reserva;
import com.polideportivo.polideportivo.entity.ReservaEquipamiento;
import com.polideportivo.polideportivo.enums.EstadoReserva;
import com.polideportivo.polideportivo.repository.EquipamientoRepository;
import com.polideportivo.polideportivo.repository.ReservaEquipamientoRepository;
import com.polideportivo.polideportivo.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReservaEquipamientoService {

    private final ReservaEquipamientoRepository reservaEquipamientoRepository;
    private final ReservaRepository reservaRepository;
    private final EquipamientoRepository equipamientoRepository;

    public ReservaEquipamientoService(
            ReservaEquipamientoRepository reservaEquipamientoRepository,
            ReservaRepository reservaRepository,
            EquipamientoRepository equipamientoRepository) {
        this.reservaEquipamientoRepository = reservaEquipamientoRepository;
        this.reservaRepository = reservaRepository;
        this.equipamientoRepository = equipamientoRepository;
    }

    public ReservaEquipamiento agregarEquipamientoAReserva(ReservaEquipamiento reservaEquipamiento) {
        validarDatos(reservaEquipamiento);

        Integer idReserva = reservaEquipamiento.getReserva().getIdReserva();
        Integer idEquipamiento = reservaEquipamiento.getEquipamiento().getIdEquipamiento();

        if (reservaEquipamientoRepository
                .existsByReserva_IdReservaAndEquipamiento_IdEquipamiento(idReserva, idEquipamiento)) {
            throw new RuntimeException("La reserva ya tiene asociado ese equipamiento");
        }

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Equipamiento equipamiento = equipamientoRepository.findById(idEquipamiento)
                .orElseThrow(() -> new RuntimeException("Equipamiento no encontrado"));

        if (!equipamiento.getActivo()) {
            throw new RuntimeException("El equipamiento no está activo");
        }

        Integer cantidadEnUso = reservaEquipamientoRepository
                .sumarCantidadEnConflicto(
                        idEquipamiento,
                        reserva.getFechaReserva(),
                        reserva.getHoraInicio(),
                        reserva.getHoraFin(),
                        List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)
                );

        Integer disponible = equipamiento.getCantidadTotal() - cantidadEnUso;

        if (reservaEquipamiento.getCantidad() > disponible) {
            throw new RuntimeException("No hay suficiente equipamiento disponible en ese horario");
        }

        reservaEquipamiento.setReserva(reserva);
        reservaEquipamiento.setEquipamiento(equipamiento);

        return reservaEquipamientoRepository.save(reservaEquipamiento);
    }

    public List<ReservaEquipamiento> obtenerTodos() {
        return reservaEquipamientoRepository.findAll();
    }

    public ReservaEquipamiento obtenerPorId(Integer id) {
        return reservaEquipamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de reserva-equipamiento no encontrado"));
    }

    public List<ReservaEquipamiento> obtenerPorReserva(Integer idReserva) {
        return reservaEquipamientoRepository.findByReserva_IdReserva(idReserva);
    }

    public List<ReservaEquipamiento> obtenerPorEquipamiento(Integer idEquipamiento) {
        return reservaEquipamientoRepository.findByEquipamiento_IdEquipamiento(idEquipamiento);
    }

    public ReservaEquipamiento obtenerPorReservaYEquipamiento(Integer idReserva, Integer idEquipamiento) {
        return reservaEquipamientoRepository
                .findByReserva_IdReservaAndEquipamiento_IdEquipamiento(idReserva, idEquipamiento)
                .orElseThrow(() -> new RuntimeException("Ese equipamiento no está asociado a la reserva"));
    }

    public ReservaEquipamiento actualizarCantidad(Integer idReservaEquipamiento, Integer nuevaCantidad) {
        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        ReservaEquipamiento existente = obtenerPorId(idReservaEquipamiento);

        Reserva reserva = existente.getReserva();
        Equipamiento equipamiento = existente.getEquipamiento();

        Integer cantidadEnUso = reservaEquipamientoRepository
                .sumarCantidadEnConflictoExcluyendoActual(
                        equipamiento.getIdEquipamiento(),
                        existente.getIdReservaEquipamiento(),
                        reserva.getFechaReserva(),
                        reserva.getHoraInicio(),
                        reserva.getHoraFin(),
                        List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)
                );

        Integer disponible = equipamiento.getCantidadTotal() - cantidadEnUso;

        if (nuevaCantidad > disponible) {
            throw new RuntimeException("No hay suficiente equipamiento disponible en ese horario");
        }

        existente.setCantidad(nuevaCantidad);
        return reservaEquipamientoRepository.save(existente);
    }

    public Integer sumarCantidadSolicitadaPorEquipamiento(Integer idEquipamiento) {
        return reservaEquipamientoRepository.sumarCantidadSolicitadaPorEquipamiento(idEquipamiento);
    }

    public void eliminarPorId(Integer id) {
        ReservaEquipamiento reservaEquipamiento = obtenerPorId(id);
        reservaEquipamientoRepository.delete(reservaEquipamiento);
    }

    public void eliminarEquipamientosDeReserva(Integer idReserva) {
        reservaEquipamientoRepository.deleteByReserva_IdReserva(idReserva);
    }

    public void eliminarEquipamientoDeReserva(Integer idReserva, Integer idEquipamiento) {
        if (!reservaEquipamientoRepository
                .existsByReserva_IdReservaAndEquipamiento_IdEquipamiento(idReserva, idEquipamiento)) {
            throw new RuntimeException("Ese equipamiento no está asociado a la reserva");
        }

        reservaEquipamientoRepository
                .deleteByReserva_IdReservaAndEquipamiento_IdEquipamiento(idReserva, idEquipamiento);
    }

    private void validarDatos(ReservaEquipamiento reservaEquipamiento) {
        if (reservaEquipamiento.getReserva() == null ||
                reservaEquipamiento.getReserva().getIdReserva() == null) {
            throw new RuntimeException("La reserva es obligatoria");
        }

        if (reservaEquipamiento.getEquipamiento() == null ||
                reservaEquipamiento.getEquipamiento().getIdEquipamiento() == null) {
            throw new RuntimeException("El equipamiento es obligatorio");
        }

        if (reservaEquipamiento.getCantidad() == null || reservaEquipamiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
    }
}