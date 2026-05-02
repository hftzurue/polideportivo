package com.polideportivo.polideportivo.controller;

import com.polideportivo.polideportivo.entity.Reserva;
import com.polideportivo.polideportivo.enums.EstadoReserva;
import com.polideportivo.polideportivo.service.ReservaService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public Reserva registrarReserva(@RequestBody Reserva reserva) {
        return reservaService.registrarReserva(reserva);
    }

    @GetMapping
    public List<Reserva> obtenerTodas() {
        return reservaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Reserva obtenerPorId(@PathVariable Integer id) {
        return reservaService.obtenerPorId(id);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Reserva> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return reservaService.obtenerPorUsuario(idUsuario);
    }

    @GetMapping("/activas")
    public List<Reserva> consultarReservasActivas() {
        return reservaService.consultarReservasActivas();
    }

    @GetMapping("/usuario/{idUsuario}/activas")
    public List<Reserva> consultarReservasActivasPorUsuario(@PathVariable Integer idUsuario) {
        return reservaService.consultarReservasActivasPorUsuario(idUsuario);
    }

    @GetMapping("/estado/{estado}")
    public List<Reserva> obtenerPorEstado(@PathVariable EstadoReserva estado) {
        return reservaService.obtenerPorEstado(estado);
    }

    @GetMapping("/espacio/{idEspacio}")
    public List<Reserva> obtenerPorEspacio(@PathVariable Integer idEspacio) {
        return reservaService.obtenerPorEspacio(idEspacio);
    }

    @GetMapping("/usuario/{idUsuario}/futuras")
    public List<Reserva> obtenerReservasFuturasPorUsuario(@PathVariable Integer idUsuario) {
        return reservaService.obtenerReservasFuturasPorUsuario(idUsuario);
    }

    @GetMapping("/fechas")
    public List<Reserva> obtenerReservasEntreFechas(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) {
        return reservaService.obtenerReservasEntreFechas(fechaInicio, fechaFin);
    }

    @PatchMapping("/{idReserva}/cancelar")
    public Reserva cancelarReserva(@PathVariable Integer idReserva) {
        return reservaService.cancelarReserva(idReserva);
    }

    @PatchMapping("/{idReserva}/confirmar")
    public Reserva confirmarReserva(@PathVariable Integer idReserva) {
        return reservaService.confirmarReserva(idReserva);
    }

    @PatchMapping("/{idReserva}/finalizar")
    public Reserva finalizarReserva(@PathVariable Integer idReserva) {
        return reservaService.finalizarReserva(idReserva);
    }

    @PatchMapping("/{idReserva}")
    public Reserva actualizarReservaParcial(@PathVariable Integer idReserva, @RequestBody Reserva reserva) {
        return reservaService.actualizarReservaParcial(idReserva, reserva);
    }

    @DeleteMapping("/{idReserva}")
    public void eliminarReserva(@PathVariable Integer idReserva) {
        reservaService.eliminarReserva(idReserva);
    }
}
