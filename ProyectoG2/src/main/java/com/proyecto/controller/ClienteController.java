/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;


import com.proyecto.domain.ClienteDTO;
import com.proyecto.repository.ClienteDao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteDao clienteDao;

    public ClienteController(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    // Listado
    @GetMapping
    public String listarClientes(Model model) {
        List<ClienteDTO> clientes = clienteDao.listarClientes();
        model.addAttribute("clientes", clientes);
        return "clientes/lista";
    }

    // Formulario nuevo cliente
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new ClienteDTO());
        return "clientes/form";
    }

    // Guardar nuevo (sp_crear_cliente)
    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") ClienteDTO clienteDTO,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "clientes/form";
        }

        try {
            clienteDao.crearCliente(clienteDTO);
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear cliente: " + e.getMessage());
            return "clientes/form";
        }

        return "redirect:/clientes";
    }

    // Formulario edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        ClienteDTO cliente = clienteDao.obtenerCliente(id);
        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    // Actualizar (sp_actualizar_cliente)
    @PostMapping("/actualizar")
    public String actualizarCliente(@Valid @ModelAttribute("cliente") ClienteDTO clienteDTO,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            return "clientes/form";
        }

        try {
            clienteDao.actualizarCliente(clienteDTO);
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar cliente: " + e.getMessage());
            return "clientes/form";
        }

        return "redirect:/clientes";
    }

    // Eliminar (sp_eliminar_cliente)
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id, Model model) {
        try {
            clienteDao.eliminarCliente(id);
        } catch (Exception e) {
            // opcional: pasar error por query param o flash attribute
            return "redirect:/clientes";
        }
        return "redirect:/clientes";
    }
}

