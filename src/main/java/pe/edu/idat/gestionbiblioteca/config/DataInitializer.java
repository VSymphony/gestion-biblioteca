package pe.edu.idat.gestionbiblioteca.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.idat.gestionbiblioteca.model.Categoria;
import pe.edu.idat.gestionbiblioteca.model.Libro;
import pe.edu.idat.gestionbiblioteca.model.Rol;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.CategoriaRepository;
import pe.edu.idat.gestionbiblioteca.repository.LibroRepository;
import pe.edu.idat.gestionbiblioteca.repository.RolRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;

// Crea datos demo al arrancar (solo si no existen todavía).
@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearUsuarioSiNoExiste("admin", "Admin", "Demo",
                "admin@biblioteca.local", "Admin12345", "ADMIN");
        crearUsuarioSiNoExiste("bibliotecario", "Bibliotecario", "Demo",
                "biblio@biblioteca.local", "Biblio12345", "BIBLIOTECARIO");
        if (categoriaRepository.count() == 0) {
            Categoria novela = crearCategoria("Novela", "Narrativa y ficción");
            Categoria cienciaFiccion = crearCategoria("Ciencia Ficción", "Futurismo y tecnología");
            Categoria historia = crearCategoria("Historia", "Ensayo histórico");
            crearLibro("Cien años de soledad", "Gabriel García Márquez", "978-0307474728", 1967, 4, novela);
            crearLibro("La ciudad y los perros", "Mario Vargas Llosa", "978-8466332936", 1963, 3, novela);
            crearLibro("1984", "George Orwell", "978-0451524935", 1949, 5, cienciaFiccion);
            crearLibro("Dune", "Frank Herbert", "978-0441013593", 1965, 2, cienciaFiccion);
            crearLibro("Sapiens", "Yuval Noah Harari", "978-0062316097", 2011, 3, historia);
        }
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

    private Categoria crearCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return categoriaRepository.save(categoria);
    }

    private void crearLibro(String titulo, String autor, String isbn,
                            Integer anio, Integer stock, Categoria categoria) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setIsbn(isbn);
        libro.setAnio(anio);
        libro.setStock(stock);
        libro.setCategoria(categoria);
        libroRepository.save(libro);
    }
}
