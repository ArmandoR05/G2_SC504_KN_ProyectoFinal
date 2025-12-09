/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.domain;

public class DetalleOrdenDTO {

    private Long detalleOrdenId;
    private Long ordenCompraId;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;

    public Long getDetalleOrdenId() {
        return detalleOrdenId;
    }

    public void setDetalleOrdenId(Long detalleOrdenId) {
        this.detalleOrdenId = detalleOrdenId;
    }

    public Long getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(Long ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

