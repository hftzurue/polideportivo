package com.polideportivo.polideportivo.controller;

import org.springframework.web.bind.annotation.RestController;
import com.polideportivo.polideportivo.entity.Espacio;
import com.polideportivo.polideportivo.service.EspacioService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/espacios")
public class EspacioController {

    private final EspacioService espacioService;

    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    @PostMapping
    public Espacio crearEspacio(@RequestBody Espacio espacio) {
        return espacioService.crearEspacio(espacio);
    }

    @GetMapping
    public List<Espacio> obtenerTodos() {
        return espacioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Espacio obtenerPorId(@PathVariable Integer id) {
        return espacioService.obtenerPorId(id);
    }

    @GetMapping("/buscar")
    public List<Espacio> buscarPorNombre(@RequestParam String nombre) {
        return espacioService.buscarPorNombre(nombre);
    }

    @GetMapping("/activos")
    public List<Espacio> obtenerActivos() {
        return espacioService.obtenerActivos();
    }

    @GetMapping("/inactivos")
    public List<Espacio> obtenerInactivos() {
        return espacioService.obtenerInactivos();
    }

    @GetMapping("/disciplina/{idDisciplina}")
    public List<Espacio> obtenerPorDisciplina(@PathVariable Integer idDisciplina) {
        return espacioService.obtenerPorDisciplina(idDisciplina);
    }

    @GetMapping("/disciplina/{idDisciplina}/activos")
    public List<Espacio> obtenerActivosPorDisciplina(@PathVariable Integer idDisciplina) {
        return espacioService.obtenerActivosPorDisciplina(idDisciplina);
    }

    @GetMapping("/capacidad")
    public List<Espacio> obtenerActivosConCapacidad(@RequestParam Integer capacidad) {
        return espacioService.obtenerActivosConCapacidad(capacidad);
    }

    @GetMapping("/horario")
    public List<Espacio> obtenerActivosAbiertosEnHorario(
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFin
    ) {
        return espacioService.obtenerActivosAbiertosEnHorario(horaInicio, horaFin);
    }

    @GetMapping("/disciplina/{idDisciplina}/horario")
    public List<Espacio> obtenerActivosPorDisciplinaYHorario(
            @PathVariable Integer idDisciplina,
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFin
    ) {
        return espacioService.obtenerActivosPorDisciplinaYHorario(idDisciplina, horaInicio, horaFin);
    }

    @PutMapping("/{id}")
    public Espacio actualizarEspacioCompleto(@PathVariable Integer id, @RequestBody Espacio espacio) {
        return espacioService.actualizarEspacioCompleto(id, espacio);
    }

    @PatchMapping("/{id}")
    public Espacio actualizarEspacioParcial(@PathVariable Integer id, @RequestBody Espacio espacio) {
        return espacioService.actualizarEspacioParcial(id, espacio);
    }

    @PatchMapping("/{id}/activar")
    public Espacio activarEspacio(@PathVariable Integer id) {
        return espacioService.activarEspacio(id);
    }

    @PatchMapping("/{id}/desactivar")
    public Espacio desactivarEspacio(@PathVariable Integer id) {
        return espacioService.desactivarEspacio(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarEspacio(@PathVariable Integer id) {
        espacioService.eliminarEspacio(id);
    }
}