package pe.edu.idat.gestionbiblioteca.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backoffice")
public class BackofficeController {

    @GetMapping
    public String panel() {
        return "backoffice/panel";
    }

    @GetMapping("/categorias")
    public String categorias() {
        return "backoffice/mantCategoria";
    }

    @GetMapping("/libros")
    public String libros() {
        return "backoffice/mantLibro";
    }

    @GetMapping("/prestamos")
    public String prestamos() {
        return "backoffice/mantPrestamo";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public String usuarios() {
        return "backoffice/mantUsuario";
    }
}
