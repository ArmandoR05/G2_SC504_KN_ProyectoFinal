/*package com.proyecto.controller;

import com.proyecto.domain.Asesoria;
import com.proyecto.domain.Usuario;
import com.proyecto.service.AsesoriaService;
import com.proyecto.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AsesoriaController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AsesoriaService asesoriaService;

    @Autowired
    private UsuarioService usuarioService;

    // Página principal de asesorías
    @GetMapping("/asesoria/asesoria")
    public String agendar(Model model) {
        var usuarios = usuarioService.getUsuarios();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("asesoria", new Asesoria());
        model.addAttribute("asesorias", asesoriaService.getAsesorias());
        model.addAttribute("mensaje", null);

        return "asesoria/asesoria";
    }

    // Guardar asesoría
    @PostMapping("/asesoria/guardar")
    public String guardar(@ModelAttribute Asesoria asesoria) {
        if (asesoria.getTipoConsulta() == null || asesoria.getTipoConsulta().isBlank()) {
            asesoria.setTipoConsulta("Consulta general"); // Valor por defecto
        }
        asesoriaService.save(asesoria);
        return "redirect:/asesoria/asesoria";
    }

    // Eliminar asesoría
    @GetMapping("/asesoria/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        Asesoria a = new Asesoria();
        a.setIdAsesoria(id);
        asesoriaService.delete(a);
        return "redirect:/asesoria/asesoria";
    }

    // Chat simulado
    @PostMapping("/asesoria/chat")
    public String procesarConsulta(@RequestParam("consulta") String consulta, Model model) {
        String respuesta;

        switch (consulta.toLowerCase()) {
            case "horarios":
            case "cuáles son los horarios":
                respuesta = "Las asesorías están disponibles de lunes a viernes de 8am a 4pm.";
                break;
            case "precio":
            case "cuánto cuesta":
                respuesta = "Las asesorías son completamente gratuitas para estudiantes.";
                break;
            case "ubicación":
                respuesta = "Las asesorías se imparten en el edificio B, aula 204.";
                break;
            default:
                respuesta = "Lo siento, no entendí tu consulta. Puedes preguntar por 'horarios', 'precio' o 'ubicación'.";
        }

        var usuarios = usuarioService.getUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("asesoria", new Asesoria());
        model.addAttribute("asesorias", asesoriaService.getAsesorias());
        model.addAttribute("mensaje", respuesta);

        return "asesoria/asesoria";
    }
}*/
