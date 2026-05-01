package com.polideportivo.polideportivo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva_equipamiento", schema = "polideportivo")
public class ReservaEquipamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReservaEquipamiento;

    @ManyToOne
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "id_equipamiento", nullable = false)
    private Equipamiento equipamiento;

    @Column(nullable = false)
    private Integer cantidad;

    public ReservaEquipamiento() {}

    public ReservaEquipamiento(Reserva reserva, Equipamiento equipamiento, Integer cantidad) {
        this.reserva = reserva;
        this.equipamiento = equipamiento;
        this.cantidad = cantidad;
    }

    public Integer getIdReservaEquipamiento() {
        return idReservaEquipamiento;
    }
    public void setIdReservaEquipamiento(Integer idReservaEquipamiento) {
        this.idReservaEquipamiento = idReservaEquipamiento; }

    public Reserva getReserva() {
        return reserva;
    }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Equipamiento getEquipamiento() {
        return equipamiento;
    }
    public void setEquipamiento(Equipamiento equipamiento) {
        this.equipamiento = equipamiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}