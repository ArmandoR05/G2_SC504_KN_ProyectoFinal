/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;


import com.proyecto.domain.ProductoDTO;
import com.proyecto.domain.ProductoStockDTO;
import com.proyecto.repository.ProductoDao;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoDao productoDao;

    public ProductoController(ProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    @GetMapping
    public String listarProductos(Model model) {
        List<ProductoDTO> productos = productoDao.listarProductos();
        model.addAttribute("productos", productos);
        return "productos/lista"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") ProductoDTO productoDTO,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            return "productos/form";
        }

        productoDao.crearProducto(productoDTO);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        ProductoDTO producto = productoDao.obtenerProductoPorId(id);
        model.addAttribute("producto", producto);
        return "productos/form";
    }

    @PostMapping("/actualizar")
    public String actualizarProducto(@Valid @ModelAttribute("producto") ProductoDTO productoDTO,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            return "productos/form";
        }

        productoDao.actualizarProducto(productoDTO);
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id) {
        productoDao.eliminarProducto(id);
        return "redirect:/productos";
    }

    @GetMapping("/inventario")
    public String verInventario(Model model) {
        List<ProductoStockDTO> lista = productoDao.listarProductosConStock();
        model.addAttribute("productosStock", lista);
        return "productos/inventario"; 
    }
}

