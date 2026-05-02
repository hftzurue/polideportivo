package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.entity.Disciplina;
import com.polideportivo.polideportivo.entity.Espacio;
import com.polideportivo.polideportivo.repository.DisciplinaRepository;
import com.polideportivo.polideportivo.repository.EspacioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class EspacioService {

    private final EspacioRepository espacioRepository;
    private final DisciplinaRepository disciplinaRepository;

    public EspacioService(EspacioRepository espacioRepository,
                          DisciplinaRepository disciplinaRepository) {
        this.espacioRepository = espacioRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public Espacio crearEspacio(Espacio espacio) {
        if (espacioRepository.existsByNombreIgnoreCase(espacio.getNombre())) {
            throw new RuntimeException("Ya existe un espacio con ese nombre");
        }

        validarDatosEspacio(espacio);

        Disciplina disciplina = disciplinaRepository
                .findById(espacio.getDisciplina().getIdDisciplina())
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));

        espacio.setDisciplina(disciplina);

        if (espacio.getActivo() == null) {
            espacio.setActivo(true);
        }

        return espacioRepository.save(espacio);
    }

    public List<Espacio> obtenerTodos() {
        return espacioRepository.findAll();
    }

    public Espacio obtenerPorId(Integer id) {
        return espacioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Espacio no encontrado"));
    }

    public Espacio obtenerPorNombre(String nombre) {
        return espacioRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Espacio no encontrado"));
    }

    public List<Espacio> buscarPorNombre(String nombre) {
        return espacioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Espacio> obtenerActivos() {
        return espacioRepository.findByActivoTrue();
    }

    public List<Espacio> obtenerInactivos() {
        return espacioRepository.findByActivoFalse();
    }

    public List<Espacio> obtenerPorDisciplina(Integer idDisciplina) {
        return espacioRepository.findByDisciplina_IdDisciplina(idDisciplina);
    }

    public List<Espacio> obtenerActivosPorDisciplina(Integer idDisciplina) {
        return espacioRepository.findByDisciplina_IdDisciplinaAndActivoTrue(idDisciplina);
    }

    public List<Espacio> obtenerActivosConCapacidad(Integer capacidad) {
        if (capacidad == null || capacidad <= 0) {
            throw new RuntimeException("La capacidad debe ser mayor a 0");
        }

        return espacioRepository.findByCapacidadGreaterThanEqualAndActivoTrue(capacidad);
    }

    public List<Espacio> obtenerActivosAbiertosEnHorario(LocalTime horaInicio, LocalTime horaFin) {
        validarHorario(horaInicio, horaFin);

        return espacioRepository
                .findByActivoTrueAndHoraAperturaLessThanEqualAndHoraCierreGreaterThanEqual(
                        horaInicio,
                        horaFin
                );
    }

    public List<Espacio> obtenerActivosPorDisciplinaYHorario(
            Integer idDisciplina,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (idDisciplina == null) {
            throw new RuntimeException("La disciplina es obligatoria");
        }

        validarHorario(horaInicio, horaFin);

        return espacioRepository
                .findByDisciplina_IdDisciplinaAndActivoTrueAndHoraAperturaLessThanEqualAndHoraCierreGreaterThanEqual(
                        idDisciplina,
                        horaInicio,
                        horaFin
                );
    }

    public Espacio actualizarEspacioCompleto(Integer id, Espacio espacio) {
        Espacio existente = obtenerPorId(id);

        if (!existente.getNombre().equalsIgnoreCase(espacio.getNombre())
                && espacioRepository.existsByNombreIgnoreCase(espacio.getNombre())) {
            throw new RuntimeException("Ya existe un espacio con ese nombre");
        }

        validarDatosEspacio(espacio);

        Disciplina disciplina = disciplinaRepository
                .findById(espacio.getDisciplina().getIdDisciplina())
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));

        existente.setNombre(espacio.getNombre());
        existente.setDescripcion(espacio.getDescripcion());
        existente.setDisciplina(disciplina);
        existente.setCapacidad(espacio.getCapacidad());
        existente.setHoraApertura(espacio.getHoraApertura());
        existente.setHoraCierre(espacio.getHoraCierre());
        existente.setPrecioHora(espacio.getPrecioHora());
        existente.setActivo(espacio.getActivo());

        return espacioRepository.save(existente);
    }

    public Espacio actualizarEspacioParcial(Integer id, Espacio espacio) {
        Espacio existente = obtenerPorId(id);

        if (espacio.getNombre() != null) {
            if (!existente.getNombre().equalsIgnoreCase(espacio.getNombre())
                    && espacioRepository.existsByNombreIgnoreCase(espacio.getNombre())) {
                throw new RuntimeException("Ya existe un espacio con ese nombre");
            }
            existente.setNombre(espacio.getNombre());
        }

        if (espacio.getDescripcion() != null) {
            existente.setDescripcion(espacio.getDescripcion());
        }

        if (espacio.getDisciplina() != null && espacio.getDisciplina().getIdDisciplina() != null) {
            Disciplina disciplina = disciplinaRepository
                    .findById(espacio.getDisciplina().getIdDisciplina())
                    .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));

            existente.setDisciplina(disciplina);
        }

        if (espacio.getCapacidad() != null) {
            if (espacio.getCapacidad() <= 0) {
                throw new RuntimeException("La capacidad debe ser mayor a 0");
            }
            existente.setCapacidad(espacio.getCapacidad());
        }

        if (espacio.getPrecioHora() != null) {
            if (espacio.getPrecioHora().signum() < 0) {
                throw new RuntimeException("El precio por hora debe ser mayor o igual a 0");
            }
            existente.setPrecioHora(espacio.getPrecioHora());
        }

        if (espacio.getHoraApertura() != null) {
            existente.setHoraApertura(espacio.getHoraApertura());
        }

        if (espacio.getHoraCierre() != null) {
            existente.setHoraCierre(espacio.getHoraCierre());
        }

        validarHorario(existente.getHoraApertura(), existente.getHoraCierre());

        if (espacio.getActivo() != null) {
            existente.setActivo(espacio.getActivo());
        }

        return espacioRepository.save(existente);
    }

    public Espacio activarEspacio(Integer id) {
        Espacio espacio = obtenerPorId(id);
        espacio.setActivo(true);
        return espacioRepository.save(espacio);
    }

    public Espacio desactivarEspacio(Integer id) {
        Espacio espacio = obtenerPorId(id);
        espacio.setActivo(false);
        return espacioRepository.save(espacio);
    }

    public void eliminarEspacio(Integer id) {
        Espacio espacio = obtenerPorId(id);
        espacioRepository.delete(espacio);
    }

    private void validarDatosEspacio(Espacio espacio) {
        if (espacio.getNombre() == null || espacio.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (espacio.getDescripcion() == null || espacio.getDescripcion().isBlank()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        if (espacio.getDisciplina() == null || espacio.getDisciplina().getIdDisciplina() == null) {
            throw new RuntimeException("La disciplina es obligatoria");
        }

        if (espacio.getCapacidad() == null || espacio.getCapacidad() <= 0) {
            throw new RuntimeException("La capacidad debe ser mayor a 0");
        }

        if (espacio.getPrecioHora() == null || espacio.getPrecioHora().signum() < 0) {
            throw new RuntimeException("El precio por hora debe ser mayor o igual a 0");
        }

        validarHorario(espacio.getHoraApertura(), espacio.getHoraCierre());
    }

    private void validarHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null || !horaInicio.isBefore(horaFin)) {
            throw new RuntimeException("El horario no es válido");
        }
    }
}