package com.polideportivo.polideportivo.controller;

import com.polideportivo.polideportivo.entity.Disciplina;
import com.polideportivo.polideportivo.service.DisciplinaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {
    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @PostMapping
    public Disciplina crearDisciplina(@RequestBody Disciplina disciplina) {
        return disciplinaService.crearDisciplina(disciplina);
    }

    @GetMapping
    public List<Disciplina> obtenerTodas() {
        return disciplinaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Disciplina obtenerPorId(@PathVariable Integer id) {
        return disciplinaService.obtenerPorId(id);
    }

    @GetMapping("/nombre/{nombre}")
    public Disciplina obtenerPorNombre(@PathVariable String nombre) {
        return disciplinaService.obtenerPorNombre(nombre);
    }

    @GetMapping("/buscar")
    public List<Disciplina> buscarPorNombre(@RequestParam String nombre) {
        return disciplinaService.buscarPorNombre(nombre);
    }

    @PutMapping("/{id}")
    public Disciplina actualizarDisciplinaCompleto(@PathVariable Integer id, @RequestBody Disciplina disciplina) {
        return disciplinaService.actualizarDisciplinaCompleto(id, disciplina);
    }

    @PatchMapping("/{id}")
    public Disciplina actualizarDisciplinaParcial(@PathVariable Integer id, @RequestBody Disciplina disciplina) {
        return disciplinaService.actualizarDisciplinaParcial(id, disciplina);
    }

    @DeleteMapping("/{id}")
    public void eliminarDisciplina(@PathVariable Integer id) {
        disciplinaService.eliminarDisciplina(id);
    }
}
