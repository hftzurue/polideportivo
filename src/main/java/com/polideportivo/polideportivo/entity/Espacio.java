package com.polideportivo.polideportivo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "espacio", schema = "polideportivo")
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEspacio;

    private String nombre;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_disciplina")
    private Disciplina disciplina;

    private Integer capacidad;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private BigDecimal precioHora;
    private Boolean activo;

    public Espacio() {}

    public Espacio(String nombre, String descripcion, Disciplina disciplina,
                   Integer capacidad, LocalTime horaApertura, LocalTime horaCierre,
                   BigDecimal precioHora, Boolean activo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.disciplina = disciplina;
        this.capacidad = capacidad;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.precioHora = precioHora;
        this.activo = activo;
    }

    public Integer getIdEspacio() { return idEspacio; }
    public void setIdEspacio(Integer idEspacio) { this.idEspacio = idEspacio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public LocalTime getHoraApertura() { return horaApertura; }
    public void setHoraApertura(LocalTime horaApertura) { this.horaApertura = horaApertura; }

    public LocalTime getHoraCierre() { return horaCierre; }
    public void setHoraCierre(LocalTime horaCierre) { this.horaCierre = horaCierre; }

    public BigDecimal getPrecioHora() { return precioHora; }
    public void setPrecioHora(BigDecimal precioHora) { this.precioHora = precioHora; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}