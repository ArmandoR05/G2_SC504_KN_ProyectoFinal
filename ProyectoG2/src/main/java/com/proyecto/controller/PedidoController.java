/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;


import com.proyecto.domain.ClienteDTO;
import com.proyecto.domain.DetallePedidoDTO;
import com.proyecto.domain.PedidoDTO;
import com.proyecto.domain.ProductoDTO;
import com.proyecto.repository.ClienteDao;
import com.proyecto.repository.PedidoDao;
import com.proyecto.repository.ProductoDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoDao pedidoDao;
    private final ClienteDao clienteDao;
    private final ProductoDao productoDao;

    public PedidoController(PedidoDao pedidoDao,
                            ClienteDao clienteDao,
                            ProductoDao productoDao) {
        this.pedidoDao = pedidoDao;
        this.clienteDao = clienteDao;
        this.productoDao = productoDao;
    }

    private List<String> getEstados() {
        return Arrays.asList("PENDIENTE", "EN PROCESO", "ENTREGADO", "CANCELADO");
    }

    @GetMapping
    public String listarPedidos(Model model) {
        List<PedidoDTO> pedidos = pedidoDao.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        PedidoDTO pedido = new PedidoDTO();
        List<ClienteDTO> clientes = clienteDao.listarClientes();

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clientes);
        model.addAttribute("estados", getEstados());

        return "pedidos/form";
    }

    @PostMapping("/guardar")
    public String guardarPedido(@RequestParam("clienteId") Long clienteId,
                                @RequestParam("estado") String estado,
                                Model model) {

        if (clienteId == null || estado == null || estado.isBlank()) {
            model.addAttribute("error", "Debe seleccionar un cliente y un estado.");
            model.addAttribute("pedido", new PedidoDTO());
            model.addAttribute("clientes", clienteDao.listarClientes());
            model.addAttribute("estados", getEstados());
            return "pedidos/form";
        }

        try {
            Long pedidoId = pedidoDao.crearPedido(clienteId, estado);
            return "redirect:/pedidos/" + pedidoId + "/detalle";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear pedido: " + e.getMessage());
            model.addAttribute("pedido", new PedidoDTO());
            model.addAttribute("clientes", clienteDao.listarClientes());
            model.addAttribute("estados", getEstados());
            return "pedidos/form";
        }
    }

    @GetMapping("/{id}/detalle")
    public String verDetallePedido(@PathVariable("id") Long pedidoId, Model model) {
        PedidoDTO pedido = pedidoDao.obtenerPedido(pedidoId);
        ClienteDTO cliente = clienteDao.obtenerCliente(pedido.getClienteId());
        List<DetallePedidoDTO> detalles = pedidoDao.listarDetalle(pedidoId);
        BigDecimal total = pedidoDao.obtenerTotalPedido(pedidoId);
        List<ProductoDTO> productos = productoDao.listarProductos();

        pedido.setNombreCliente(cliente.getNombre());
        pedido.setTotalPedido(total);

        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productos", productos);
        model.addAttribute("estados", getEstados());

        return "pedidos/detalle";
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable("id") Long pedidoId,
                                @RequestParam("estado") String nuevoEstado) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado);
        return "redirect:/pedidos/" + pedidoId + "/detalle";
    }

    @PostMapping("/{id}/detalle/agregar")
    public String agregarDetalle(@PathVariable("id") Long pedidoId,
                                 @RequestParam("productoId") Long productoId,
                                 @RequestParam("cantidad") Integer cantidad) {
        pedidoDao.agregarDetalle(pedidoId, productoId, cantidad);
        return "redirect:/pedidos/" + pedidoId + "/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable("id") Long pedidoId) {
        try {
            pedidoDao.eliminarPedido(pedidoId);
        } catch (Exception e) {
        }
        return "redirect:/pedidos";
    }
}

