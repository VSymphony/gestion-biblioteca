package pe.edu.idat.gestionbiblioteca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.idat.gestionbiblioteca.model.Rol;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.RolRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario getUsuarioByNomusuario(String nomusuario) {
        return usuarioRepository.findByNomusuario(nomusuario);
    }

    public Usuario saveUser(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Rol rol = rolRepository.findByNomrol("LECTOR")
                .orElseThrow(() -> new IllegalStateException("No existe el rol LECTOR"));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
}
