package pe.edu.idat.gestionbiblioteca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.gestionbiblioteca.dto.Respuesta;
import pe.edu.idat.gestionbiblioteca.dto.UsuarioSeguridadDto;
import pe.edu.idat.gestionbiblioteca.model.Rol;
import pe.edu.idat.gestionbiblioteca.model.Usuario;
import pe.edu.idat.gestionbiblioteca.repository.RolRepository;
import pe.edu.idat.gestionbiblioteca.repository.UsuarioRepository;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @GetMapping("/listar")
    public Respuesta listar() {
        return new Respuesta(true, null, usuarioRepository.findAll(Sort.by("nomusuario")));
    }

    @GetMapping("/roles")
    public Respuesta roles() {
        return new Respuesta(true, null, rolRepository.findAll(Sort.by("idrol")));
    }

    @PostMapping("/estado")
    public Respuesta estado(@RequestParam(required = false) Integer idusuario,
                            @RequestParam(required = false) Boolean activo,
                            @AuthenticationPrincipal UsuarioSeguridadDto admin) {
        if (idusuario == null || activo == null) {
            return new Respuesta(false, "Datos incompletos");
        }
        Usuario usuario = usuarioRepository.findById(idusuario).orElse(null);
        if (usuario == null) {
            return new Respuesta(false, "El usuario no existe");
        }
        if (usuario.getNomusuario().equals(admin.getUsername()) && !activo) {
            return new Respuesta(false, "No puedes desactivar tu propio usuario");
        }
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
        return new Respuesta(true, activo
                ? "Usuario activado correctamente"
                : "Usuario desactivado correctamente");
    }

    @PostMapping("/rol")
    public Respuesta rol(@RequestParam(required = false) Integer idusuario,
                         @RequestParam(required = false) Integer idrol,
                         @AuthenticationPrincipal UsuarioSeguridadDto admin) {
        if (idusuario == null || idrol == null) {
            return new Respuesta(false, "Datos incompletos");
        }
        Usuario usuario = usuarioRepository.findById(idusuario).orElse(null);
        if (usuario == null) {
            return new Respuesta(false, "El usuario no existe");
        }
        if (usuario.getNomusuario().equals(admin.getUsername())) {
            return new Respuesta(false, "No puedes cambiar tu propio rol");
        }
        Rol rol = rolRepository.findById(idrol).orElse(null);
        if (rol == null) {
            return new Respuesta(false, "El rol no existe");
        }
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
        return new Respuesta(true, "Rol actualizado correctamente");
    }
}
