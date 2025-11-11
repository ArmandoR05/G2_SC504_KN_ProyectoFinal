/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.domain;

/**
 *
 * @author PC
 */
public record ProductoDTO(
    Long productoId,
    String nombre,
    String categoria,
    String descripcion,
    Double precio,
    Integer cantidadActual
) {}

