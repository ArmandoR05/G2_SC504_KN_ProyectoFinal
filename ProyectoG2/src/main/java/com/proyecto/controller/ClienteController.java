/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

import com.proyecto.domain.ClienteDTO;
import com.proyecto.repository.ClienteRepository;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository repo;

    public ClienteController(ClienteRepository repo) {
        this.repo = repo;
    }

    // LISTA
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", repo.listar());
        return "clientes/lista";
    }

    // FORM CREAR
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("cliente", new ClienteDTO(null, "", "", "", ""));
        return "clientes/crear";
    }

    // GUARDAR NUEVO
    @PostMapping("/crear")
    public String crear(@RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            RedirectAttributes ra) {

        Long id = repo.crear(nombre, correo, telefono, direccion);
        ra.addFlashAttribute("ok", "Cliente creado (#" + id + ")");
        return "redirect:/clientes";
    }

    // FORM EDITAR
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<ClienteDTO> cli = repo.obtenerPorId(id);
        if (cli.isEmpty()) {
            ra.addFlashAttribute("err", "No existe el cliente");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cli.get());
        return "clientes/editar";
    }

    // ACTUALIZAR
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String direccion,
            RedirectAttributes ra) {

        repo.actualizar(id, nombre, correo, telefono, direccion);
        ra.addFlashAttribute("ok", "Cliente actualizado");
        return "redirect:/clientes";
    }

    // ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        repo.eliminar(id);
        ra.addFlashAttribute("ok", "Cliente eliminado");
        return "redirect:/clientes";
    }
}
