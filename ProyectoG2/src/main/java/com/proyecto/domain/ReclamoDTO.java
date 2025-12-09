/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.domain;

/**
 *
 * @author PC
 */

public class ReclamoDTO {

    private Long reclamoId;
    private Long atencionId;
    private Long clienteId;
    private String nombreCliente;
    private Long detallePedidoId;
    private String tipoReclamo;
    private String descripcion;
    private String estado;

    public Long getReclamoId() {
        return reclamoId;
    }

    public void setReclamoId(Long reclamoId) {
        this.reclamoId = reclamoId;
    }

    public Long getAtencionId() {
        return atencionId;
    }

    public void setAtencionId(Long atencionId) {
        this.atencionId = atencionId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Long getDetallePedidoId() {
        return detallePedidoId;
    }

    public void setDetallePedidoId(Long detallePedidoId) {
        this.detallePedidoId = detallePedidoId;
    }

    public String getTipoReclamo() {
        return tipoReclamo;
    }

    public void setTipoReclamo(String tipoReclamo) {
        this.tipoReclamo = tipoReclamo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

