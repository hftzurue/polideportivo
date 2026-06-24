package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Disciplina;
import com.polideportivo.polideportivo.repository.DisciplinaRepository;
import com.polideportivo.polideportivo.repository.EspacioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;
    private final EspacioRepository espacioRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, EspacioRepository espacioRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.espacioRepository = espacioRepository;
    }

    public Disciplina crearDisciplina(Disciplina disciplina) {
        if (disciplinaRepository.existsByNombreIgnoreCase(disciplina.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una disciplina con ese nombre");
        }

        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> obtenerTodas() {
        return disciplinaRepository.findAll();
    }

    public Disciplina obtenerPorId(Integer id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Disciplina no encontrada"));
    }

    public Disciplina obtenerPorNombre(String nombre) {
        return disciplinaRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Disciplina no encontrada"));
    }

    public List<Disciplina> buscarPorNombre(String nombre) {
        return disciplinaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Disciplina actualizarDisciplinaCompleto(Integer id, Disciplina disciplina) {
        Disciplina existente = obtenerPorId(id);

        if (!existente.getNombre().equalsIgnoreCase(disciplina.getNombre())
                && disciplinaRepository.existsByNombreIgnoreCase(disciplina.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una disciplina con ese nombre");
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
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una disciplina con ese nombre");
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
        if (espacioRepository.existsByDisciplina_IdDisciplina(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pudo eliminar la disciplina porque existen espacios asociados a esta"
            );
        }

        disciplinaRepository.delete(disciplina);
    }
}
