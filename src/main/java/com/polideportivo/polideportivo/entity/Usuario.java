package com.polideportivo.polideportivo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.polideportivo.polideportivo.enums.Rol;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "usuario", schema = "polideportivo")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String correo;

    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    public Usuario() {}

    public Usuario(String nombre, String primerApellido, String segundoApellido, String correo, String nombreUsuario, String contrasena, Rol rol) {
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.correo = correo;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNombreUsuario(){ return this.nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario){ this.nombreUsuario = nombreUsuario; }

    public String getContrasena(){ return this.contrasena; }
    public void setContrasena(String contrasena){ this.contrasena = contrasena; }
}