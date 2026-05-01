package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Disciplina;
import com.polideportivo.polideportivo.repository.DisciplinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }

    public Disciplina crearDisciplina(Disciplina disciplina) {
        if (disciplinaRepository.existsByNombreIgnoreCase(disciplina.getNombre())) {
            throw new RuntimeException("Ya existe una disciplina con ese nombre");
        }

        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> obtenerTodas() {
        return disciplinaRepository.findAll();
    }

    public Disciplina obtenerPorId(Integer id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));
    }

    public Disciplina obtenerPorNombre(String nombre) {
        return disciplinaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));
    }

    public List<Disciplina> buscarPorNombre(String nombre) {
        return disciplinaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Disciplina actualizarDisciplinaCompleto(Integer id, Disciplina disciplina) {
        Disciplina existente = obtenerPorId(id);

        if (!existente.getNombre().equalsIgnoreCase(disciplina.getNombre())
                && disciplinaRepository.existsByNombreIgnoreCase(disciplina.getNombre())) {
            throw new RuntimeException("Ya existe una disciplina con ese nombre");
        }

        existente.setNombre(disciplina.getNombre());
        existente.setDescripcion(disciplina.getDescripcion());

        return disciplinaRepository.save(existente);
    }

    public Disciplina actualizarDisciplinaParcial(Integer id, Disciplina disciplina) {
        Disciplina existente = obtenerPorId(id);

        if (disciplina.getNombre() != null) {
            if (!existente.getNombre().equalsIgnoreCase(disciplina.getNombre())
                    && disciplinaRepository.existsByNombreIgnoreCase(disciplina.getNombre())) {
                throw new RuntimeException("Ya existe una disciplina con ese nombre");
            }

            existente.setNombre(disciplina.getNombre());
        }

        if (disciplina.getDescripcion() != null) {
            existente.setDescripcion(disciplina.getDescripcion());
        }

        return disciplinaRepository.save(existente);
    }

    public void eliminarDisciplina(Integer id) {
        Disciplina disciplina = obtenerPorId(id);
        disciplinaRepository.delete(disciplina);
    }
}