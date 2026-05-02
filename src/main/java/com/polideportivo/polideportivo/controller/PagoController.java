package com.polideportivo.polideportivo.controller;

import org.springframework.web.bind.annotation.RestController;
import com.polideportivo.polideportivo.entity.Pago;
import com.polideportivo.polideportivo.enums.EstadoPago;
import com.polideportivo.polideportivo.enums.MetodoPago;
import com.polideportivo.polideportivo.service.PagoService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public Pago crearPago(@RequestBody Pago pago) {
        return pagoService.crearPago(pago);
    }

    @GetMapping
    public List<Pago> obtenerTodos() {
        return pagoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Pago obtenerPorId(@PathVariable Integer id) {
        return pagoService.obtenerPorId(id);
    }

    @GetMapping("/reserva/{idReserva}")
    public Pago obtenerPorReserva(@PathVariable Integer idReserva) {
        return pagoService.obtenerPorReserva(idReserva);
    }

    @GetMapping("/estado/{estadoPago}")
    public List<Pago> obtenerPorEstado(@PathVariable EstadoPago estadoPago) {
        return pagoService.obtenerPorEstado(estadoPago);
    }

    @GetMapping("/metodo/{metodoPago}")
    public List<Pago> obtenerPorMetodo(@PathVariable MetodoPago metodoPago) {
        return pagoService.obtenerPorMetodo(metodoPago);
    }

    @GetMapping("/estado-y-metodo")
    public List<Pago> obtenerPorEstadoYMetodo(
            @RequestParam EstadoPago estadoPago,
            @RequestParam MetodoPago metodoPago
    ) {
        return pagoService.obtenerPorEstadoYMetodo(estadoPago, metodoPago);
    }

    @GetMapping("/fechas")
    public List<Pago> obtenerEntreFechas(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin
    ) {
        return pagoService.obtenerEntreFechas(fechaInicio, fechaFin);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Pago> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return pagoService.obtenerPorUsuario(idUsuario);
    }

    @GetMapping("/espacio/{idEspacio}")
    public List<Pago> obtenerPorEspacio(@PathVariable Integer idEspacio) {
        return pagoService.obtenerPorEspacio(idEspacio);
    }

    @GetMapping("/fecha-reserva/{fechaReserva}")
    public List<Pago> obtenerPorFechaReserva(@PathVariable LocalDate fechaReserva) {
        return pagoService.obtenerPorFechaReserva(fechaReserva);
    }

    @PatchMapping("/{idPago}/estado")
    public Pago actualizarEstadoPago(
            @PathVariable Integer idPago,
            @RequestParam EstadoPago estadoPago
    ) {
        return pagoService.actualizarEstadoPago(idPago, estadoPago);
    }

    @DeleteMapping("/{id}")
    public void eliminarPago(@PathVariable Integer id) {
        pagoService.eliminarPago(id);
    }
}