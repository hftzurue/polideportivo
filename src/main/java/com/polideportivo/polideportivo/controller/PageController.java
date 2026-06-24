package com.polideportivo.polideportivo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "forward:/templates/login.html";
    }

    @GetMapping("/registro")
    public String registro() {
        return "forward:/templates/registro.html";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "forward:/templates/inicio.html";
    }

    @GetMapping("/disciplinas-vista")
    public String disciplinasVista() {
        return "forward:/templates/disciplinas.html";
    }

    @GetMapping("/espacios-disponibles")
    public String espaciosDisponibles() {
        return "forward:/templates/espacios.html";
    }

    @GetMapping("/reservar")
    public String reservar() {
        return "forward:/templates/reservar.html";
    }

    @GetMapping("/mis-reservas")
    public String misReservas() {
        return "forward:/templates/mis-reservas.html";
    }

    @GetMapping("/mi-perfil")
    public String miPerfil() {
        return "forward:/templates/mi-perfil.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/templates/admin.html";
    }

    @GetMapping("/admin-disciplina")
    public String adminDisciplina() {
        return "forward:/templates/admin-disciplina.html";
    }

    @GetMapping("/admin-espacio")
    public String adminEspacio() {
        return "forward:/templates/admin-espacio.html";
    }

    @GetMapping("/admin-equipamiento")
    public String adminEquipamiento() {
        return "forward:/templates/admin-equipamiento.html";
    }

    @GetMapping("/admin-reserva")
    public String adminReserva() {
        return "forward:/templates/admin-reserva.html";
    }

    @GetMapping("/admin-usuarios")
    public String adminUsuarios() {
        return "forward:/templates/admin-usuarios.html";
    }

    @GetMapping("/admin-pagos")
    public String adminPagos() {
        return "forward:/templates/admin-pagos.html";
    }
}
