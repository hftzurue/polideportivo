package com.polideportivo.polideportivo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "equipamiento", schema = "polideportivo")
public class Equipamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEquipamiento;

    private String nombre;
    private Integer cantidadTotal;

    @ManyToOne
    @JoinColumn(name = "id_disciplina")
    private Disciplina disciplina;

    private Boolean activo;

    public Equipamiento() {}

    public Equipamiento(String nombre, Integer cantidadTotal,
                        Disciplina disciplina, Boolean activo) {
        this.nombre = nombre;
        this.cantidadTotal = cantidadTotal;
        this.disciplina = disciplina;
        this.activo = activo;
    }

    public Integer getIdEquipamiento() {
        return idEquipamiento;
    }
    public void setIdEquipamiento(Integer idEquipamiento) {
        this.idEquipamiento = idEquipamiento;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }
    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }
    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
