
package com.calletuning.inventario.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.calletuning.inventario.entity.Usuario;
import com.calletuning.inventario.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado"
                ));

        String nombreRol = usuario.getRol().getNombre().trim()
                .toUpperCase();

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(nombreRol)
                .disabled(!Boolean.TRUE.equals(usuario.getEstado()))
                .build();
    }
}
