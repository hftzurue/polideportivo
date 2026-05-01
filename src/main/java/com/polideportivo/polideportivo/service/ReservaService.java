package com.polideportivo.polideportivo.service;

import com.polideportivo.polideportivo.repository.EspacioRepository;
import com.polideportivo.polideportivo.repository.ReservaRepository;
import com.polideportivo.polideportivo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, EspacioRepository espacioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }

}
