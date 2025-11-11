/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ProductoConsultaRepository {

    private final JdbcTemplate jdbc;
    private final SimpleJdbcCall spListar;
    private final SimpleJdbcCall spBuscarNombre;
    private final SimpleJdbcCall spBuscarCategoria;

    public ProductoConsultaRepository(DataSource ds) {
        this.jdbc = new JdbcTemplate(ds);

        this.spListar = new SimpleJdbcCall(ds)
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("LISTAR")
                .returningResultSet("P_CURSOR", new ProductoRowMapper());

        this.spBuscarNombre = new SimpleJdbcCall(ds)
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("BUSCAR_POR_NOMBRE")
                .returningResultSet("P_CURSOR", new ProductoRowMapper());

        this.spBuscarCategoria = new SimpleJdbcCall(ds)
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("BUSCAR_POR_CATEGORIA")
                .returningResultSet("P_CURSOR", new ProductoRowMapper());
    }

    public List<Map<String,Object>> viewProductos() {
        return jdbc.query("SELECT producto_id, nombre, categoria, precio FROM vw_productos ORDER BY producto_id DESC",
                (rs, i) -> rowAsMap(rs));
    }

    public List<Map<String,Object>> spListar() {
        Map<String, Object> out = spListar.execute(new HashMap<>());
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> lista = (List<Map<String,Object>>) out.get("P_CURSOR");
        return lista;
    }

    public List<Map<String,Object>> spBuscarPorNombre(String texto) {
        Map<String, Object> in = new HashMap<>();
        in.put("P_TEXTO", texto);
        Map<String, Object> out = spBuscarNombre.execute(in);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> lista = (List<Map<String,Object>>) out.get("P_CURSOR");
        return lista;
    }

    public List<Map<String,Object>> spBuscarPorCategoria(String categoria) {
        Map<String, Object> in = new HashMap<>();
        in.put("P_CATEGORIA", categoria);
        Map<String, Object> out = spBuscarCategoria.execute(in);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> lista = (List<Map<String,Object>>) out.get("P_CURSOR");
        return lista;
    }

    public String fnNombreProducto(Long id) {
        return jdbc.queryForObject("SELECT fn_nombre_producto(?) FROM dual", String.class, id);
    }

    public Double fnPrecioProducto(Long id) {
        return jdbc.queryForObject("SELECT fn_producto_precio(?) FROM dual", Double.class, id);
    }

    public Double fnValorInventarioProducto(Long id) {
        return jdbc.queryForObject("SELECT fn_valor_inventario_producto(?) FROM dual", Double.class, id);
    }

    private static Map<String,Object> rowAsMap(ResultSet rs) throws SQLException {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("producto_id", rs.getLong("producto_id"));
        m.put("nombre", rs.getString("nombre"));
        m.put("categoria", rs.getString("categoria"));
        try { m.put("precio", rs.getBigDecimal("precio")); } catch (SQLException ignore) {}
        try { m.put("descripcion", rs.getString("descripcion")); } catch (SQLException ignore) {}
        return m;
    }

    private static class ProductoRowMapper implements RowMapper<Map<String,Object>> {
        @Override public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rowAsMap(rs);
        }
    }
}

