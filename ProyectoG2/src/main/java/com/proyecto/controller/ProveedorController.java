/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

import com.proyecto.domain.ProveedorDTO;
import com.proyecto.repository.ProveedorDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorDao proveedorDao;

    public ProveedorController(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
    }

    @GetMapping
    public String listar(Model model) {
        List<ProveedorDTO> proveedores = proveedorDao.listarProveedores();
        model.addAttribute("proveedores", proveedores);
        return "proveedores/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new ProveedorDTO());
        return "proveedores/form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
            @RequestParam String telefono,
            @RequestParam String correo) {

        proveedorDao.crearProveedor(nombre, telefono, correo);
        return "redirect:/proveedores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        ProveedorDTO proveedor = proveedorDao.obtenerProveedor(id);
        model.addAttribute("proveedor", proveedor);
        return "proveedores/form";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long proveedorId,
            @RequestParam String nombre,
            @RequestParam String telefono,
            @RequestParam String correo) {

        proveedorDao.actualizarProveedor(proveedorId, nombre, telefono, correo);
        return "redirect:/proveedores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        try {
            proveedorDao.eliminarProveedor(id);
        } catch (Exception e) {
            
        }
        return "redirect:/proveedores";
    }

}
