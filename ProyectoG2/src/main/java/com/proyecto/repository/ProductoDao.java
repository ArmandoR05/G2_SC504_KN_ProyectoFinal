/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import com.proyecto.domain.ProductoDTO;
import com.proyecto.domain.ProductoStockDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ProductoDao {

    private final JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall spCrearProducto;
    private SimpleJdbcCall spActualizarProducto;
    private SimpleJdbcCall spEliminarProducto;
    private SimpleJdbcCall spListarProductos;

    public ProductoDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.spCrearProducto = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PRODUCTO_APP")
                .withProcedureName("SP_CREAR_PRODUCTO");

        this.spActualizarProducto = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PRODUCTO_APP")
                .withProcedureName("SP_ACTUALIZAR_PRODUCTO");

        this.spEliminarProducto = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PRODUCTO_APP")
                .withProcedureName("SP_ELIMINAR_PRODUCTO");

        this.spListarProductos = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PRODUCTO_APP")
                .withProcedureName("SP_LISTAR_PRODUCTOS");
    }

    public Long crearProducto(ProductoDTO dto) {
        Map<String, Object> out = spCrearProducto.execute(
                dto.getNombre(),
                dto.getCategoria(),
                dto.getDescripcion(),
                dto.getPrecio(),
                null
        );

        Object idOut = out.get("P_PRODUCTO_ID_O");
        if (idOut == null) {
            return null;
        }
        return ((Number) idOut).longValue();
    }

    public void actualizarProducto(ProductoDTO dto) {
        spActualizarProducto.execute(
                dto.getProductoId(),
                dto.getNombre(),
                dto.getCategoria(),
                dto.getDescripcion(),
                dto.getPrecio()
        );
    }

    public void eliminarProducto(Long id) {
        spEliminarProducto.execute(id);
    }

    @SuppressWarnings("unchecked")
    public List<ProductoDTO> listarProductos() {
        Map<String, Object> out = spListarProductos.execute();

        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");

        List<ProductoDTO> lista = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                ProductoDTO dto = new ProductoDTO();
                dto.setProductoId(((Number) row.get("PRODUCTO_ID")).longValue());
                dto.setNombre((String) row.get("NOMBRE"));
                dto.setCategoria((String) row.get("CATEGORIA"));
                dto.setDescripcion((String) row.get("DESCRIPCION"));
                dto.setPrecio((java.math.BigDecimal) row.get("PRECIO"));
                lista.add(dto);
            }
        }
        return lista;
    }

    public List<ProductoStockDTO> listarProductosConStock() {
        String sql = "SELECT PRODUCTO_ID, NOMBRE, CATEGORIA, DESCRIPCION, PRECIO, CANTIDAD_ACTUAL " +
                     "FROM VW_PRODUCTOS_CON_STOCK";

        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
            ProductoStockDTO dto = new ProductoStockDTO();
            dto.setProductoId(rs.getLong("PRODUCTO_ID"));
            dto.setNombre(rs.getString("NOMBRE"));
            dto.setCategoria(rs.getString("CATEGORIA"));
            dto.setDescripcion(rs.getString("DESCRIPCION"));
            dto.setPrecio(rs.getBigDecimal("PRECIO"));
            dto.setCantidadActual(rs.getInt("CANTIDAD_ACTUAL"));
            return dto;
        });
    }

    public ProductoDTO obtenerProductoPorId(Long id) {
        String sql = "SELECT PRODUCTO_ID, NOMBRE, CATEGORIA, DESCRIPCION, PRECIO " +
                     "FROM PRODUCTO WHERE PRODUCTO_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setProductoId(rs.getLong("PRODUCTO_ID"));
            dto.setNombre(rs.getString("NOMBRE"));
            dto.setCategoria(rs.getString("CATEGORIA"));
            dto.setDescripcion(rs.getString("DESCRIPCION"));
            dto.setPrecio(rs.getBigDecimal("PRECIO"));
            return dto;
        }, id);
    }
}
