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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public String usuarios() {
        return "backoffice/usuarios";
    }
}
