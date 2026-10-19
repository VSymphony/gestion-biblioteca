package pe.edu.idat.gestionbiblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.idat.gestionbiblioteca.dto.UsuarioSeguridadDto;
import pe.edu.idat.gestionbiblioteca.model.Rol;
import pe.edu.idat.gestionbiblioteca.model.Usuario;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioService.getUsuarioByNomusuario(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        return getUsuarioSeguridadDto(
                usuario,
                getAuthorities(usuario.getRol())
        );
    }

    private List<GrantedAuthority> getAuthorities(Rol rol) {

        if (rol == null || rol.getNomrol() == null) {
            return Collections.emptyList();
        }
        String nomrol = rol.getNomrol().startsWith("ROLE_")
                ? rol.getNomrol()
                : "ROLE_" + rol.getNomrol();
        return List.of(
                new SimpleGrantedAuthority(nomrol)
        );
    }

    private UsuarioSeguridadDto getUsuarioSeguridadDto(
            Usuario usuario,
            List<GrantedAuthority> authorities) {

        UsuarioSeguridadDto dto = new UsuarioSeguridadDto(
                usuario.getNomusuario(),
                usuario.getPassword(),
                usuario.getActivo(),
                true,
                true,
                true,
                authorities
        );

        dto.setEmail(usuario.getEmail());
        dto.setNombres(usuario.getNombres());

        return dto;
    }
}
