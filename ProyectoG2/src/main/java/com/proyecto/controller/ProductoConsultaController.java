/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

import com.proyecto.repository.ProductoConsultaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos/consultas")
public class ProductoConsultaController {

    private final ProductoConsultaRepository repo;

    public ProductoConsultaController(ProductoConsultaRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/vw")
    public String verVistaProductos(Model model) {
        model.addAttribute("titulo", "Vista: vw_productos");
        model.addAttribute("productos", repo.viewProductos());
        return "productos/visibles/vw-productos";
    }

    @GetMapping("/listar")
    public String listarSP(Model model) {
        model.addAttribute("titulo", "Procedimiento: pkg_producto.listar");
        model.addAttribute("productos", repo.spListar());
        return "productos/visibles/listar-proc";
    }

    @GetMapping("/buscar-nombre")
    public String buscarNombreForm(
            @RequestParam(value = "q", defaultValue = "") String q,
            Model model) {
        model.addAttribute("titulo", "Procedimiento: pkg_producto.buscar_por_nombre");
        model.addAttribute("q", q);
        if (!q.isBlank()) {
            model.addAttribute("productos", repo.spBuscarPorNombre(q));
        }
        return "productos/visibles/buscar-nombre";
    }

    @GetMapping("/buscar-categoria")
    public String buscarCategoriaForm(
            @RequestParam(value = "cat", defaultValue = "") String cat,
            Model model) {
        model.addAttribute("titulo", "Procedimiento: pkg_producto.buscar_por_categoria");
        model.addAttribute("cat", cat);
        if (!cat.isBlank()) {
            model.addAttribute("productos", repo.spBuscarPorCategoria(cat));
        }
        return "productos/visibles/buscar-categoria";
    }

    @GetMapping("/detalle/{id}")
    public String detalleFunciones(@PathVariable Long id, Model model) {
        model.addAttribute("titulo", "Funciones visibles (detalle de producto)");
        model.addAttribute("id", id);
        model.addAttribute("nombre", repo.fnNombreProducto(id));
        model.addAttribute("precio", repo.fnPrecioProducto(id));
        model.addAttribute("valorInventario", repo.fnValorInventarioProducto(id));
        return "productos/visibles/detalle-funciones";
    }
}
