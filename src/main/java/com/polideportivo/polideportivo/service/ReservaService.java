package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Espacio;
import com.polideportivo.polideportivo.entity.Reserva;
import com.polideportivo.polideportivo.entity.Usuario;
import com.polideportivo.polideportivo.enums.EstadoReserva;
import com.polideportivo.polideportivo.repository.EspacioRepository;
import com.polideportivo.polideportivo.repository.ReservaRepository;
import com.polideportivo.polideportivo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, EspacioRepository espacioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }
    public Reserva registrarReserva(Reserva reserva) {
        Integer idUsuario = reserva.getUsuario().getIdUsuario();
        Integer idEspacio = reserva.getEspacio().getIdEspacio();

        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Espacio espacio = espacioRepository.findById(idEspacio).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espacio no encontrado"));

        if (!espacio.getActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El espacio no está activo");
        }

        if (reserva.getCantidadPersonas() > espacio.getCapacidad()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad de personas supera la capacidad del espacio");
        }

        if (reserva.getHoraInicio().isBefore(espacio.getHoraApertura())
                || reserva.getHoraFin().isAfter(espacio.getHoraCierre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva está fuera del horario permitido del espacio");
        }

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser menor que la hora de fin");
        }

        //Estados que hacen que una reserva se bloquee, porque ya esta reservado
        List<EstadoReserva> estadosQueBloquean = List.of(
                EstadoReserva.PENDIENTE,
                EstadoReserva.CONFIRMADA
        );

        //Lista de reservas que tienen conflictos con la reserva que se quiere solicitar
        Long ReservasConflictos = reservaRepository.contarReservasEnConflicto(idEspacio, reserva.getFechaReserva(), reserva.getHoraInicio(), reserva.getHoraFin(), estadosQueBloquean);

        if (ReservasConflictos > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una reserva en ese horario");
        }

        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setFechaCreacion(LocalDateTime.now());

        if (reserva.getEstado() == null) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }

        if (reserva.getMontoTotal() == null) {
            long horas = ChronoUnit.HOURS.between(reserva.getHoraInicio(), reserva.getHoraFin());

            reserva.setMontoTotal(espacio.getPrecioHora().multiply(BigDecimal.valueOf(horas)));
        }

        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public Reserva obtenerPorId(Integer id) {
        return reservaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reserva no encontrada"));
    }

    public List<Reserva> obtenerPorUsuario(Integer idUsuario) {
        return reservaRepository.findByUsuario_IdUsuarioOrderByFechaReservaAscHoraInicioAsc(idUsuario);
    }

    public List<Reserva> consultarReservasActivas() {
        return reservaRepository.findByEstadoInOrderByFechaReservaAscHoraInicioAsc(obtenerEstadosActivos());
    }

    public List<Reserva> consultarReservasActivasPorUsuario(Integer idUsuario) {
        return reservaRepository.findByUsuario_IdUsuarioAndEstadoInOrderByFechaReservaAscHoraInicioAsc(idUsuario, obtenerEstadosActivos());
    }

    public List<Reserva> obtenerPorEstado(EstadoReserva estado) {
        return reservaRepository.findByEstado(estado);
    }

    public List<Reserva> obtenerPorEspacio(Integer idEspacio) {
        return reservaRepository.findByEspacio_IdEspacio(idEspacio);
    }

    public List<Reserva> obtenerReservasFuturasPorUsuario(Integer idUsuario) {
        return reservaRepository.findByUsuario_IdUsuarioAndFechaReservaGreaterThanEqualOrderByFechaReservaAscHoraInicioAsc(
                idUsuario,
                LocalDate.now()
        );
    }

    public List<Reserva> obtenerReservasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findByFechaReservaBetweenOrderByFechaReservaAscHoraInicioAsc(
                fechaInicio,
                fechaFin
        );
    }

    public Reserva cancelarReserva(Integer idReserva) {
        Reserva reserva = obtenerPorId(idReserva);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La reserva ya esta cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede cancelar una reserva finalizada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        return reservaRepository.save(reserva);
    }

    public Reserva confirmarReserva(Integer idReserva) {
        Reserva reserva = obtenerPorId(idReserva);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede confirmar una reserva cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede confirmar una reserva finalizada");
        }
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            return reserva;
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return reservaRepository.save(reserva);
    }

    public Reserva finalizarReserva(Integer idReserva) {
        Reserva reserva = obtenerPorId(idReserva);

        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede finalizar una reserva pendiente");
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No se puede finalizar una reserva cancelada");
        }
        if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
            return reserva;
        }

        reserva.setEstado(EstadoReserva.FINALIZADA);

        return reservaRepository.save(reserva);
    }

    public Reserva actualizarReservaParcial(Integer idReserva, Reserva reserva) {
        Reserva existente = obtenerPorId(idReserva);

        if (reserva.getCantidadPersonas() != null) {
            existente.setCantidadPersonas(reserva.getCantidadPersonas());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de CantidadPersonas nulo");
        }
        if (reserva.getFechaReserva() != null) {
            existente.setFechaReserva(reserva.getFechaReserva());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de FechaReserva nulo");
        }
        if (reserva.getHoraInicio() != null) {
            existente.setHoraInicio(reserva.getHoraInicio());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de HoraInicio nulo");
        }
        if (reserva.getHoraFin() != null) {
            existente.setHoraFin(reserva.getHoraFin());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de HoraFin nulo");
        }
        if (reserva.getEstado() != null) {
            existente.setEstado(reserva.getEstado());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de Estado nulo");
        }
        if (reserva.getMontoTotal() != null) {
            existente.setMontoTotal(reserva.getMontoTotal());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de MontoTotal nulo");
        }

        return reservaRepository.save(existente);
    }

    public void eliminarReserva(Integer idReserva) {
        Reserva reserva = obtenerPorId(idReserva);
        reservaRepository.delete(reserva);
    }

    private List<EstadoReserva> obtenerEstadosActivos() {
        return List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);
    }
}
