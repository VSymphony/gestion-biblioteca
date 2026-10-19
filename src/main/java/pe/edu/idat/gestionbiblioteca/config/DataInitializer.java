package pe.edu.idat.gestionbiblioteca.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.idat.gestionbiblioteca.model.Rol;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.RolRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;

// Crea usuarios demo al arrancar (solo si no existen todavía).
@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearUsuarioSiNoExiste("admin", "Admin", "Demo",
                "admin@biblioteca.local", "Admin12345", "ADMIN");
        crearUsuarioSiNoExiste("bibliotecario", "Bibliotecario", "Demo",
                "biblio@biblioteca.local", "Biblio12345", "BIBLIOTECARIO");
    }

    private void crearUsuarioSiNoExiste(String nomusuario, String nombres,
                                        String apellidos, String email,
                                        String password, String nomrol) {
        if (usuarioRepository.findByNomusuario(nomusuario) != null) {
            return;
        }
        Rol rol = rolRepository.findByNomrol(nomrol)
                .orElseThrow(() -> new IllegalStateException("No existe el rol " + nomrol));
        Usuario usuario = new Usuario();
        usuario.setNomusuario(nomusuario);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setActivo(true);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
