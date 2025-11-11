/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

import org.springframework.ui.Model;
import com.proyecto.domain.ProductoDTO;
import com.proyecto.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author PC
 */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository repo;

    public ProductoController(ProductoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", repo.listar());
        return "productos/lista";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("producto", new ProductoDTO(null, "", "", "", 0.0, 0));
        return "productos/crear";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam String descripcion,
            @RequestParam Double precio,
            RedirectAttributes ra) {

        Long id = repo.crear(nombre, categoria, descripcion, precio);
        ra.addFlashAttribute("ok", "Producto creado (#" + id + ")");
        return "redirect:/productos";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repo.obtenerPorId(id)
                .map(p -> {
                    model.addAttribute("producto", p);
                    return "productos/editar";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("err", "No existe el producto");
                    return "redirect:/productos";
                });
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam String descripcion,
            @RequestParam Double precio,
            RedirectAttributes ra) {
        repo.actualizar(id, nombre, categoria, descripcion, precio);
        ra.addFlashAttribute("ok", "Producto actualizado");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        repo.eliminar(id);
        ra.addFlashAttribute("ok", "Producto eliminado");
        return "redirect:/productos";
    }
}
