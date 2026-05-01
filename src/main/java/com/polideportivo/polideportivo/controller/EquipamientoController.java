package com.polideportivo.polideportivo.controller;

import com.polideportivo.polideportivo.entity.Equipamiento;
import com.polideportivo.polideportivo.service.EquipamientoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamientos")
public class EquipamientoController {
    private final EquipamientoService equipamientoService;

    public EquipamientoController(EquipamientoService equipamientoService) {
        this.equipamientoService = equipamientoService;
    }

    @PostMapping
    public Equipamiento crearEquipamiento(@RequestBody Equipamiento equipamiento) {
        return equipamientoService.crearEquipamiento(equipamiento);
    }

    @GetMapping
    public List<Equipamiento> obtenerTodos() {
        return equipamientoService.obtenerTodos();
    }

    @GetMapping("/activos")
    public List<Equipamiento> obtenerActivos() {
        return equipamientoService.obtenerActivos();
    }

    @GetMapping("/inactivos")
    public List<Equipamiento> obtenerInactivos() {
        return equipamientoService.obtenerInactivos();
    }

    @GetMapping("/{id}")
    public Equipamiento obtenerPorId(@PathVariable Integer id) {
        return equipamientoService.obtenerPorId(id);
    }

    @GetMapping("/nombre/{nombre}")
    public Equipamiento obtenerPorNombre(@PathVariable String nombre) {
        return equipamientoService.obtenerPorNombre(nombre);
    }

    @GetMapping("/buscar")
    public List<Equipamiento> buscarPorNombre(@RequestParam String nombre) {
        return equipamientoService.buscarPorNombre(nombre);
    }

    @GetMapping("/disciplina/{idDisciplina}/activos")
    public List<Equipamiento> obtenerActivosPorDisciplina(@PathVariable Integer idDisciplina) {
        return equipamientoService.obtenerActivosPorDisciplina(idDisciplina);
    }

    @GetMapping("/disciplina/{idDisciplina}")
    public List<Equipamiento> obtenerPorDisciplina(@PathVariable Integer idDisciplina) {
        return equipamientoService.obtenerPorDisciplina(idDisciplina);
    }

    @PutMapping("/{id}")
    public Equipamiento actualizarEquipamientoCompleto(@PathVariable Integer id, @RequestBody Equipamiento equipamiento) {
        return equipamientoService.actualizarEquipamientoCompleto(id, equipamiento);
    }

    @PatchMapping("/{id}")
    public Equipamiento actualizarEquipamientoParcial(@PathVariable Integer id, @RequestBody Equipamiento equipamiento) {
        return equipamientoService.actualizarEquipamientoParcial(id, equipamiento);
    }

    @PatchMapping("/{id}/desactivar")
    public Equipamiento desactivarEquipamiento(@PathVariable Integer id) {
        return equipamientoService.desactivarEquipamiento(id);
    }

    @PatchMapping("/{id}/activar")
    public Equipamiento activarEquipamiento(@PathVariable Integer id) {
        return equipamientoService.activarEquipamiento(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarEquipamiento(@PathVariable Integer id) {
        equipamientoService.eliminarEquipamiento(id);
    }
}
