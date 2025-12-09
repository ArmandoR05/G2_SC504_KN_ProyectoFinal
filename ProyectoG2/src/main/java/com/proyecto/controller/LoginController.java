/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

/**
 *
 * @author PC
 */
import com.proyecto.domain.LoginDTO;
import com.proyecto.model.Usuario;
import com.proyecto.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String mostrarIndex(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "index";   
    }

    @GetMapping("/login")
    public String redirigirLogin() {
        return "redirect:/";
    }

    @PostMapping("/login")
    public String procesarLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                                BindingResult result,
                                Model model,
                                HttpSession session) {

        if (result.hasErrors()) {
            return "index";   
        }

        Optional<Usuario> usuarioOpt = usuarioRepository
                .findByNombreUsuarioIgnoreCaseAndContrasenaAndEstado(
                        loginDTO.getNombreUsuario(),
                        loginDTO.getContrasena(),
                        "ACTIVO"
                );

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("errorLogin", "Usuario o contraseña incorrectos, o usuario inactivo.");
            return "index";   
        }

        Usuario usuario = usuarioOpt.get();

        session.setAttribute("usuarioLogueado", usuario);

        
        return "redirect:/menu";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
