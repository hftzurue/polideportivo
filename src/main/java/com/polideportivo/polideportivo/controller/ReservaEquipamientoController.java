package com.polideportivo.polideportivo.controller;

import com.polideportivo.polideportivo.entity.ReservaEquipamiento;
import com.polideportivo.polideportivo.service.ReservaEquipamientoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reserva-equipamientos")
public class ReservaEquipamientoController {

    private final ReservaEquipamientoService reservaEquipamientoService;

    public ReservaEquipamientoController(ReservaEquipamientoService reservaEquipamientoService) {
        this.reservaEquipamientoService = reservaEquipamientoService;
    }

    @PostMapping
    public ReservaEquipamiento agregarEquipamientoAReserva(@RequestBody ReservaEquipamiento reservaEquipamiento) {
        return reservaEquipamientoService.agregarEquipamientoAReserva(reservaEquipamiento);
    }

    @GetMapping
    public List<ReservaEquipamiento> obtenerTodos() {
        return reservaEquipamientoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ReservaEquipamiento obtenerPorId(@PathVariable Integer id) {
        return reservaEquipamientoService.obtenerPorId(id);
    }

    @GetMapping("/reserva/{idReserva}")
    public List<ReservaEquipamiento> obtenerPorReserva(@PathVariable Integer idReserva) {
        return reservaEquipamientoService.obtenerPorReserva(idReserva);
    }

    @GetMapping("/equipamiento/{idEquipamiento}")
    public List<ReservaEquipamiento> obtenerPorEquipamiento(@PathVariable Integer idEquipamiento) {
        return reservaEquipamientoService.obtenerPorEquipamiento(idEquipamiento);
    }

    @GetMapping("/reserva/{idReserva}/equipamiento/{idEquipamiento}")
    public ReservaEquipamiento obtenerPorReservaYEquipamiento(
            @PathVariable Integer idReserva,
            @PathVariable Integer idEquipamiento
    ) {
        return reservaEquipamientoService.obtenerPorReservaYEquipamiento(idReserva, idEquipamiento);
    }

    @PatchMapping("/{id}/cantidad")
    public ReservaEquipamiento actualizarCantidad(
            @PathVariable Integer id,
            @RequestParam Integer cantidad
    ) {
        return reservaEquipamientoService.actualizarCantidad(id, cantidad);
    }

    @GetMapping("/equipamiento/{idEquipamiento}/cantidad-solicitada")
    public Integer sumarCantidadSolicitadaPorEquipamiento(@PathVariable Integer idEquipamiento) {
        return reservaEquipamientoService.sumarCantidadSolicitadaPorEquipamiento(idEquipamiento);
    }

    @DeleteMapping("/{id}")
    public void eliminarPorId(@PathVariable Integer id) {
        reservaEquipamientoService.eliminarPorId(id);
    }

    @DeleteMapping("/reserva/{idReserva}")
    public void eliminarEquipamientosDeReserva(@PathVariable Integer idReserva) {
        reservaEquipamientoService.eliminarEquipamientosDeReserva(idReserva);
    }

    @DeleteMapping("/reserva/{idReserva}/equipamiento/{idEquipamiento}")
    public void eliminarEquipamientoDeReserva(
            @PathVariable Integer idReserva,
            @PathVariable Integer idEquipamiento) {
        reservaEquipamientoService.eliminarEquipamientoDeReserva(idReserva, idEquipamiento);
    }
}