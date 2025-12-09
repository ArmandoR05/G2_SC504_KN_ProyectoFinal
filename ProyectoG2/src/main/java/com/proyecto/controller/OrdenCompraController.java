/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

import com.proyecto.domain.DetalleOrdenDTO;
import com.proyecto.domain.OrdenCompraDTO;
import com.proyecto.domain.ProductoDTO;
import com.proyecto.domain.ProductoProveedorDTO;
import com.proyecto.domain.ProveedorDTO;
import com.proyecto.repository.OrdenCompraDao;
import com.proyecto.repository.ProveedorDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/ordenescompra")
public class OrdenCompraController {

    private final OrdenCompraDao ordenCompraDao;
    private final ProveedorDao proveedorDao;

    public OrdenCompraController(OrdenCompraDao ordenCompraDao,
            ProveedorDao proveedorDao) {
        this.ordenCompraDao = ordenCompraDao;
        this.proveedorDao = proveedorDao;
    }

    private List<String> getEstados() {
        return Arrays.asList("SOLICITADA", "EN PROCESO", "RECIBIDA", "CANCELADA");
    }

    @GetMapping
    public String listar(Model model) {
        List<OrdenCompraDTO> ordenes = ordenCompraDao.listarOrdenes();
        model.addAttribute("ordenes", ordenes);
        return "ordenescompra/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        OrdenCompraDTO oc = new OrdenCompraDTO();
        List<ProveedorDTO> proveedores = proveedorDao.listarProveedores();
        model.addAttribute("orden", oc);
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("estados", getEstados());
        return "ordenescompra/form";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("proveedorId") Long proveedorId,
            @RequestParam("estado") String estado,
            Model model) {

        if (proveedorId == null || estado == null || estado.isBlank()) {
            model.addAttribute("error", "Debe seleccionar un proveedor y un estado.");
            model.addAttribute("orden", new OrdenCompraDTO());
            model.addAttribute("proveedores", proveedorDao.listarProveedores());
            model.addAttribute("estados", getEstados());
            return "ordenescompra/form";
        }

        Long ocId = ordenCompraDao.crearOrdenCompra(proveedorId, estado);
        return "redirect:/ordenescompra/" + ocId + "/detalle";
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable("id") Long ordenId,
            @RequestParam("estado") String nuevoEstado) {
        ordenCompraDao.actualizarEstado(ordenId, nuevoEstado);
        return "redirect:/ordenescompra/" + ordenId + "/detalle";
    }

    @PostMapping("/{id}/detalle/agregar")
    public String agregarDetalle(@PathVariable("id") Long ordenId,
            @RequestParam("productoId") Long productoId,
            @RequestParam("cantidad") Integer cantidad) {
        ordenCompraDao.agregarDetalle(ordenId, productoId, cantidad);
        return "redirect:/ordenescompra/" + ordenId + "/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long ordenId) {
        try {
            ordenCompraDao.eliminarOrdenCompra(ordenId);
        } catch (Exception e) {
        }
        return "redirect:/ordenescompra";
    }

    @PostMapping("/{id}/detalle/actualizar")
    public String actualizarDetalle(@PathVariable("id") Long ordenId,
            @RequestParam("detalleOrdenId") Long detalleOrdenId,
            @RequestParam("cantidad") Integer cantidad) {

        ordenCompraDao.actualizarDetalle(detalleOrdenId, cantidad);
        return "redirect:/ordenescompra/" + ordenId + "/detalle";
    }

    @GetMapping("/{id}/detalle/eliminar/{detalleId}")
    public String eliminarDetalle(@PathVariable("id") Long ordenId,
            @PathVariable("detalleId") Long detalleId) {

        ordenCompraDao.eliminarDetalle(detalleId);
        return "redirect:/ordenescompra/" + ordenId + "/detalle";
    }

    @GetMapping("/{id}/detalle")
    public String detalle(@PathVariable("id") Long ordenId, Model model) {

        OrdenCompraDTO orden = ordenCompraDao.obtenerOrden(ordenId);
        List<DetalleOrdenDTO> detalles = ordenCompraDao.listarDetalle(ordenId);
        List<ProductoProveedorDTO> productosProveedor
                = ordenCompraDao.listarProductosProveedor(orden.getProveedorId());

        List<ProductoDTO> productosNoAsociados
                = ordenCompraDao.listarProductosNoAsociadosProveedor(orden.getProveedorId());

        model.addAttribute("orden", orden);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productosProveedor", productosProveedor);
        model.addAttribute("productosNoAsociados", productosNoAsociados);
        model.addAttribute("estados", getEstados());

        return "ordenescompra/detalle";
    }

    @PostMapping("/{id}/detalle/asociar-producto")
    public String asociarProductoProveedor(@PathVariable("id") Long ordenId,
            @RequestParam("productoId") Long productoId) {

        OrdenCompraDTO orden = ordenCompraDao.obtenerOrden(ordenId);

        ordenCompraDao.asociarProductoProveedor(orden.getProveedorId(), productoId);

        return "redirect:/ordenescompra/" + ordenId + "/detalle";
    }

    @PostMapping("/{id}/detalle/eliminar-asociacion")
    public String eliminarAsociacion(@PathVariable("id") Long ordenId,
            @RequestParam("productoId") Long productoId) {

        OrdenCompraDTO orden = ordenCompraDao.obtenerOrden(ordenId);

        ordenCompraDao.eliminarAsociacionProductoProveedor(
                orden.getProveedorId(),
                productoId
        );

        return "redirect:/ordenescompra/" + ordenId + "/detalle";
    }

}
