package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Disciplina;
import com.polideportivo.polideportivo.entity.Equipamiento;
import com.polideportivo.polideportivo.repository.DisciplinaRepository;
import com.polideportivo.polideportivo.repository.EquipamientoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class EquipamientoService {
    private final EquipamientoRepository equipamientoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public EquipamientoService(EquipamientoRepository equipamientoRepository, DisciplinaRepository disciplinaRepository) {
        this.equipamientoRepository = equipamientoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public Equipamiento crearEquipamiento(Equipamiento equipamiento) {
        if (equipamientoRepository.existsByNombreIgnoreCase(equipamiento.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un equipamiento con ese nombre");
        }

        Integer idDisciplina = equipamiento.getDisciplina().getIdDisciplina();

        Disciplina disciplina = disciplinaRepository.findById(idDisciplina)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina no encontrada"));

        equipamiento.setDisciplina(disciplina);

        if (equipamiento.getActivo() == null) {
            equipamiento.setActivo(true);
        }

        return equipamientoRepository.save(equipamiento);
    }

    public List<Equipamiento> obtenerTodos() {
        return equipamientoRepository.findAll();
    }

    public List<Equipamiento> obtenerActivos() {
        return equipamientoRepository.findByActivoTrue();
    }

    public List<Equipamiento> obtenerInactivos() {
        return equipamientoRepository.findByActivoFalse();
    }

    public Equipamiento obtenerPorId(Integer id) {
        return equipamientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipamiento no encontrado"));
    }

    public Equipamiento obtenerPorNombre(String nombre) {
        return equipamientoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipamiento no encontrado"));
    }

    public List<Equipamiento> buscarPorNombre(String nombre) {
        return equipamientoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Equipamiento> obtenerActivosPorDisciplina(Integer idDisciplina) {
        return equipamientoRepository.findByDisciplina_IdDisciplinaAndActivoTrue(idDisciplina);
    }

    public List<Equipamiento> obtenerPorDisciplina(Integer idDisciplina) {
        return equipamientoRepository.findByDisciplina_IdDisciplina(idDisciplina);
    }

    public Equipamiento actualizarEquipamientoCompleto(Integer id, Equipamiento equipamiento) {
        Equipamiento existente = obtenerPorId(id);

        if (!existente.getNombre().equalsIgnoreCase(equipamiento.getNombre())
                && equipamientoRepository.existsByNombreIgnoreCase(equipamiento.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un equipamiento con ese nombre");
        }

        Disciplina disciplina = disciplinaRepository.findById(
                equipamiento.getDisciplina().getIdDisciplina()
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina no encontrada"));

        existente.setNombre(equipamiento.getNombre());
        existente.setCantidadTotal(equipamiento.getCantidadTotal());
        existente.setDisciplina(disciplina);
        existente.setActivo(equipamiento.getActivo());

        return equipamientoRepository.save(existente);
    }

    public Equipamiento actualizarEquipamientoParcial(Integer id, Equipamiento equipamiento) {
        Equipamiento existente = obtenerPorId(id);

        if (equipamiento.getNombre() != null) {
            if (!existente.getNombre().equalsIgnoreCase(equipamiento.getNombre())
                    && equipamientoRepository.existsByNombreIgnoreCase(equipamiento.getNombre())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un equipamiento con ese nombre");
            }

            existente.setNombre(equipamiento.getNombre());
        }

        if (equipamiento.getCantidadTotal() != null) {
            existente.setCantidadTotal(equipamiento.getCantidadTotal());
        }

        if (equipamiento.getDisciplina() != null && equipamiento.getDisciplina().getIdDisciplina() != null) {
            Disciplina disciplina = disciplinaRepository.findById(
                    equipamiento.getDisciplina().getIdDisciplina()
            ).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina no encontrada"));

            existente.setDisciplina(disciplina);
        }

        if (equipamiento.getActivo() != null) {
            existente.setActivo(equipamiento.getActivo());
        }

        return equipamientoRepository.save(existente);
    }

    public Equipamiento desactivarEquipamiento(Integer id) {
        Equipamiento equipamiento = obtenerPorId(id);
        equipamiento.setActivo(false);
        return equipamientoRepository.save(equipamiento);
    }

    public Equipamiento activarEquipamiento(Integer id) {
        Equipamiento equipamiento = obtenerPorId(id);
        equipamiento.setActivo(true);
        return equipamientoRepository.save(equipamiento);
    }

    public void eliminarEquipamiento(Integer id) {
        Equipamiento equipamiento = obtenerPorId(id);
        equipamientoRepository.delete(equipamiento);
    }
}
