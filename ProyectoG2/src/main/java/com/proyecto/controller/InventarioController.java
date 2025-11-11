package com.proyecto.controller;

import com.proyecto.domain.InventarioDTO;
import com.proyecto.repository.InventarioRepository;
import com.proyecto.repository.ProductoRepository;
import java.util.HashMap;
import java.util.Map;
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
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioRepository invRepo;
    private final ProductoRepository prodRepo;

    public InventarioController(InventarioRepository invRepo, ProductoRepository prodRepo) {
        this.invRepo = invRepo;
        this.prodRepo = prodRepo;
    }

    @GetMapping
    public String listar(Model model) {
        var inventarios = invRepo.listar();
        var productos = prodRepo.listar();

        Map<Long, String> nombres = new HashMap<>();
        for (var p : productos) {
            nombres.put(p.productoId(), p.nombre());
        }

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("nombres", nombres);

        return "inventario/lista";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable("id") Long productoId,
            Model model,
            RedirectAttributes ra) {

        Optional<InventarioDTO> item = invRepo.obtenerPorId(productoId);

        if (item.isEmpty()) {
            ra.addFlashAttribute("err", "No existe inventario para el producto #" + productoId);
            return "redirect:/inventario";
        }

        var productos = prodRepo.listar();
        Map<Long, String> nombres = new HashMap<>();
        for (var p : productos) {
            nombres.put(p.productoId(), p.nombre());
        }

        model.addAttribute("item", item.get());
        model.addAttribute("nombres", nombres);

        return "inventario/editar";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable("id") Long productoId,
            @RequestParam("cantidad") Integer cantidad,
            RedirectAttributes ra) {

        invRepo.actualizar(productoId, cantidad);
        ra.addFlashAttribute("ok", "Inventario actualizado correctamente");
        return "redirect:/inventario";
    }

    
}
