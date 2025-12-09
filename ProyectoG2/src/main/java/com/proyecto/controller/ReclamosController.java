/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.controller;

import com.proyecto.domain.AtencionDTO;
import com.proyecto.domain.ReclamoDTO;
import com.proyecto.domain.DevolucionDTO;
import com.proyecto.domain.ClienteDTO;
import com.proyecto.domain.PedidoDTO;
import com.proyecto.repository.ReclamosDao;
import com.proyecto.repository.ClienteDao;
import com.proyecto.repository.PedidoDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/reclamos")
public class ReclamosController {

    private final ReclamosDao reclamosDao;
    private final ClienteDao clienteDao;
    private final PedidoDao pedidoDao;

    public ReclamosController(ReclamosDao reclamosDao,
            ClienteDao clienteDao,
            PedidoDao pedidoDao) {
        this.reclamosDao = reclamosDao;
        this.clienteDao = clienteDao;
        this.pedidoDao = pedidoDao;
    }

    private List<String> getEstadosAtencion() {
        return Arrays.asList("ABIERTA", "EN PROCESO", "CERRADA");
    }

    private List<String> getTiposAtencion() {
        return Arrays.asList("RECLAMO", "CONSULTA", "OTRO");
    }

    private List<String> getTiposReclamo() {
        return Arrays.asList("PRODUCTO DAÑADO", "PRODUCTO EQUIVOCADO", "OTRO");
    }

    private List<String> getEstadosReclamo() {
        return Arrays.asList("PENDIENTE", "EN PROCESO", "RESUELTO", "RECHAZADO");
    }

    private List<String> getEstadosDevolucion() {
        return Arrays.asList("PENDIENTE", "APROBADA", "RECHAZADA");
    }

    @GetMapping("/atenciones")
    public String listarAtenciones(Model model) {
        List<AtencionDTO> atenciones = reclamosDao.listarAtenciones();
        model.addAttribute("atenciones", atenciones);
        model.addAttribute("estadosAtencion", getEstadosAtencion());
        return "reclamos/atenciones-lista";
    }

    @GetMapping("/atenciones/nueva")
    public String nuevaAtencion(@RequestParam(value = "clienteId", required = false) Long clienteId,
            Model model) {

        List<ClienteDTO> clientes = clienteDao.listarClientes();
        List<PedidoDTO> pedidos;

        if (clienteId != null) {
            pedidos = pedidoDao.listarPedidosPorCliente(clienteId);
            model.addAttribute("clienteSeleccionadoId", clienteId);
        } else {
            pedidos = List.of(); 
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("tiposAtencion", getTiposAtencion());
        model.addAttribute("estadosAtencion", getEstadosAtencion());

        return "reclamos/atenciones-form";
    }

    @PostMapping("/atenciones/guardar")
    public String guardarAtencion(@RequestParam("clienteId") Long clienteId,
            @RequestParam("pedidoId") Long pedidoId,
            @RequestParam("tipo") String tipo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("estado") String estado) {

        reclamosDao.crearAtencion(clienteId, pedidoId, tipo, descripcion, estado);
        return "redirect:/reclamos/atenciones";
    }

    @PostMapping("/atenciones/{id}/cambiar-estado")
    public String cambiarEstadoAtencion(@PathVariable("id") Long atencionId,
            @RequestParam("estado") String nuevoEstado) {
        reclamosDao.actualizarEstadoAtencion(atencionId, nuevoEstado);
        return "redirect:/reclamos/atenciones";
    }

    @GetMapping
    public String listarReclamos(Model model) {
        List<ReclamoDTO> reclamos = reclamosDao.listarReclamos();
        List<AtencionDTO> atenciones = reclamosDao.listarAtenciones(); 

        model.addAttribute("reclamos", reclamos);
        model.addAttribute("atenciones", atenciones);
        model.addAttribute("estadosReclamo", getEstadosReclamo());
        return "reclamos/reclamos-lista";
    }

    @GetMapping("/nuevo")
    public String nuevoReclamo(Model model) {
        List<AtencionDTO> atenciones = reclamosDao.listarAtenciones();

        model.addAttribute("atenciones", atenciones);
        model.addAttribute("tiposReclamo", getTiposReclamo());
        model.addAttribute("estadosReclamo", getEstadosReclamo());

        return "reclamos/reclamos-form";
    }

    @PostMapping("/guardar")
    public String guardarReclamo(@RequestParam("atencionId") Long atencionId,
            @RequestParam("tipoReclamo") String tipoReclamo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("estado") String estado) {

        Long detallePedidoId = reclamosDao.obtenerDetallePedidoPorAtencion(atencionId);

        reclamosDao.crearReclamo(atencionId, detallePedidoId, tipoReclamo, descripcion, estado);

        return "redirect:/reclamos";
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstadoReclamo(@PathVariable("id") Long reclamoId,
            @RequestParam("estado") String nuevoEstado) {
        reclamosDao.actualizarEstadoReclamo(reclamoId, nuevoEstado);
        return "redirect:/reclamos";
    }

    @GetMapping("/devoluciones")
    public String listarDevoluciones(Model model) {
        List<DevolucionDTO> devoluciones = reclamosDao.listarDevoluciones();
        List<ReclamoDTO> reclamos = reclamosDao.listarReclamos();

        model.addAttribute("devoluciones", devoluciones);
        model.addAttribute("reclamos", reclamos);
        model.addAttribute("estadosDevolucion", getEstadosDevolucion());

        return "reclamos/devoluciones-lista";
    }

    @GetMapping("/devoluciones/nueva")
    public String nuevaDevolucion(Model model) {
        List<ReclamoDTO> reclamos = reclamosDao.listarReclamos();

        model.addAttribute("reclamos", reclamos);
        model.addAttribute("estadosDevolucion", getEstadosDevolucion());

        return "reclamos/devoluciones-form";
    }

    @PostMapping("/devoluciones/guardar")
    public String guardarDevolucion(@RequestParam("reclamoId") Long reclamoId,
            @RequestParam("motivo") String motivo,
            @RequestParam("estado") String estado) {

        ReclamoDTO reclamo = reclamosDao.obtenerReclamo(reclamoId);
        Long detallePedidoId = reclamo.getDetallePedidoId();

        reclamosDao.crearDevolucion(detallePedidoId, reclamoId, motivo, estado);

        return "redirect:/reclamos/devoluciones";
    }

    @GetMapping("/editar/{id}")
    public String editarReclamo(@PathVariable("id") Long reclamoId, Model model) {

        ReclamoDTO reclamo = reclamosDao.obtenerReclamo(reclamoId);

        model.addAttribute("reclamo", reclamo);
        model.addAttribute("tiposReclamo", getTiposReclamo());
        model.addAttribute("estadosReclamo", getEstadosReclamo());

        return "reclamos/reclamos-form";
    }

    @PostMapping("/actualizar")
    public String actualizarReclamo(@RequestParam("reclamoId") Long reclamoId,
            @RequestParam("tipoReclamo") String tipoReclamo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("estado") String estado) {

        reclamosDao.actualizarReclamo(reclamoId, tipoReclamo, descripcion, estado);
        return "redirect:/reclamos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarReclamo(@PathVariable("id") Long reclamoId) {
        try {
            reclamosDao.eliminarReclamo(reclamoId);
        } catch (Exception e) {
        }
        return "redirect:/reclamos";
    }

    @GetMapping("/devoluciones/editar/{id}")
    public String editarDevolucion(@PathVariable("id") Long devolucionId, Model model) {

        DevolucionDTO devolucion = reclamosDao.obtenerDevolucion(devolucionId);
        List<ReclamoDTO> reclamos = reclamosDao.listarReclamos();

        model.addAttribute("devolucion", devolucion);
        model.addAttribute("reclamos", reclamos);
        model.addAttribute("estadosDevolucion", getEstadosDevolucion());

        return "reclamos/devoluciones-form";
    }

    @PostMapping("/devoluciones/actualizar")
    public String actualizarDevolucion(@RequestParam("devolucionId") Long devolucionId,
            @RequestParam("motivo") String motivo,
            @RequestParam("estado") String estado) {

        reclamosDao.actualizarDevolucion(devolucionId, motivo, estado);
        return "redirect:/reclamos/devoluciones";
    }

    @GetMapping("/devoluciones/eliminar/{id}")
    public String eliminarDevolucion(@PathVariable("id") Long devolucionId) {
        try {
            reclamosDao.eliminarDevolucion(devolucionId);
        } catch (Exception e) {
        }
        return "redirect:/reclamos/devoluciones";
    }

    @GetMapping("/atenciones/editar/{id}")
    public String editarAtencion(@PathVariable("id") Long atencionId, Model model) {

        AtencionDTO atencion = reclamosDao.obtenerAtencion(atencionId);
        List<String> tipos = getTiposAtencion();

        model.addAttribute("atencion", atencion);
        model.addAttribute("tiposAtencion", tipos);
        model.addAttribute("estadosAtencion", getEstadosAtencion());

        return "reclamos/atenciones-form";
    }

    @PostMapping("/atenciones/actualizar")
    public String actualizarAtencion(@RequestParam("atencionId") Long atencionId,
            @RequestParam("tipo") String tipo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("estado") String estado) {

        reclamosDao.actualizarAtencion(atencionId, tipo, descripcion, estado);
        return "redirect:/reclamos/atenciones";
    }

    @GetMapping("/atenciones/eliminar/{id}")
    public String eliminarAtencion(@PathVariable("id") Long atencionId) {
        try {
            reclamosDao.eliminarAtencion(atencionId);
        } catch (Exception e) {
        }
        return "redirect:/reclamos/atenciones";
    }

}
