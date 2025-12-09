/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import com.proyecto.domain.DetalleOrdenDTO;
import com.proyecto.domain.OrdenCompraDTO;
import com.proyecto.domain.ProductoDTO;
import com.proyecto.domain.ProductoProveedorDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class OrdenCompraDao {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall spCrearOrden;
    private final SimpleJdbcCall spActualizarEstado;
    private final SimpleJdbcCall spListarOrdenes;
    private final SimpleJdbcCall spAgregarDetalle;
    private final SimpleJdbcCall spListarDetalle;
    private final SimpleJdbcCall spListarProductosProveedor;
    private final SimpleJdbcCall spEliminarOrden;
    private final SimpleJdbcCall spActualizarDetalle;
    private final SimpleJdbcCall spEliminarDetalle;
    private final SimpleJdbcCall spAsociarProductoProveedor;
    private final SimpleJdbcCall spEliminarAsociacionProductoProveedor;

    public OrdenCompraDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.spCrearOrden = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_CREAR_ORDEN_COMPRA");

        this.spActualizarEstado = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ACTUALIZAR_ESTADO_ORDEN");

        this.spListarOrdenes = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_LISTAR_ORDENES_COMPRA")
                .returningResultSet("P_CURSOR", new OrdenRowMapper());

        this.spAgregarDetalle = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_AGREGAR_DETALLE_ORDEN");

        this.spListarDetalle = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_LISTAR_DETALLE_ORDEN")
                .returningResultSet("P_CURSOR", new DetalleRowMapper());

        this.spListarProductosProveedor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_LISTAR_PRODUCTOS_PROVEEDOR")
                .returningResultSet("P_CURSOR", new ProductoProveedorRowMapper());

        this.spEliminarOrden = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ELIMINAR_ORDEN_COMPRA");
        this.spActualizarDetalle = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ACTUALIZAR_DETALLE_ORDEN");

        this.spEliminarDetalle = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ELIMINAR_DETALLE_ORDEN");
        this.spAsociarProductoProveedor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ASOCIAR_PRODUCTO_PROVEEDOR");
        this.spEliminarAsociacionProductoProveedor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ELIMINAR_ASOCIACION_PRODUCTO_PROVEEDOR");

    }

    private static class OrdenRowMapper implements RowMapper<OrdenCompraDTO> {

        @Override
        public OrdenCompraDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrdenCompraDTO dto = new OrdenCompraDTO();
            dto.setOrdenCompraId(rs.getLong("ORDEN_COMPRA_ID"));
            dto.setProveedorId(rs.getLong("PROVEEDOR_ID"));
            dto.setNombreProveedor(rs.getString("NOMBRE_PROVEEDOR"));
            dto.setFecha(rs.getTimestamp("FECHA"));
            dto.setEstado(rs.getString("ESTADO"));
            return dto;
        }
    }

    private static class DetalleRowMapper implements RowMapper<DetalleOrdenDTO> {

        @Override
        public DetalleOrdenDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            DetalleOrdenDTO dto = new DetalleOrdenDTO();
            dto.setDetalleOrdenId(rs.getLong("DETALLE_ORDEN_ID"));
            dto.setOrdenCompraId(rs.getLong("ORDEN_COMPRA_ID"));
            dto.setProductoId(rs.getLong("PRODUCTO_ID"));
            dto.setNombreProducto(rs.getString("NOMBRE_PRODUCTO"));
            dto.setCantidad(rs.getInt("CANTIDAD"));
            return dto;
        }
    }

    private static class ProductoProveedorRowMapper implements RowMapper<ProductoProveedorDTO> {

        @Override
        public ProductoProveedorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductoProveedorDTO dto = new ProductoProveedorDTO();
            dto.setProveedorId(rs.getLong("PROVEEDOR_ID"));
            dto.setNombreProveedor(rs.getString("NOMBRE_PROVEEDOR"));
            dto.setProductoId(rs.getLong("PRODUCTO_ID"));
            dto.setNombreProducto(rs.getString("NOMBRE_PRODUCTO"));
            return dto;
        }
    }

    public Long crearOrdenCompra(Long proveedorId, String estado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PROVEEDOR_ID", proveedorId)
                .addValue("P_ESTADO", estado);

        Map<String, Object> out = spCrearOrden.execute(params);
        Object idOut = out.get("P_ORDEN_COMPRA_ID_O");
        if (idOut == null) {
            return null;
        }
        return ((Number) idOut).longValue();
    }

    public void actualizarEstado(Long ordenCompraId, String nuevoEstado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ORDEN_COMPRA_ID", ordenCompraId)
                .addValue("P_NUEVO_ESTADO", nuevoEstado);
        spActualizarEstado.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<OrdenCompraDTO> listarOrdenes() {
        Map<String, Object> out = spListarOrdenes.execute();
        return (List<OrdenCompraDTO>) out.get("P_CURSOR");
    }

    public OrdenCompraDTO obtenerOrden(Long ordenCompraId) {
        String sql = "SELECT o.ORDEN_COMPRA_ID, o.PROVEEDOR_ID, p.NOMBRE AS NOMBRE_PROVEEDOR, "
                + "o.FECHA, o.ESTADO "
                + "FROM ORDENCOMPRA o JOIN PROVEEDOR p ON p.PROVEEDOR_ID = o.PROVEEDOR_ID "
                + "WHERE o.ORDEN_COMPRA_ID = ?";
        return jdbcTemplate.queryForObject(sql, new OrdenRowMapper(), ordenCompraId);
    }

    public void agregarDetalle(Long ordenCompraId, Long productoId, Integer cantidad) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ORDEN_COMPRA_ID", ordenCompraId)
                .addValue("P_PRODUCTO_ID", productoId)
                .addValue("P_CANTIDAD", cantidad);

        spAgregarDetalle.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<DetalleOrdenDTO> listarDetalle(Long ordenCompraId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ORDEN_COMPRA_ID", ordenCompraId);

        Map<String, Object> out = spListarDetalle.execute(params);
        return (List<DetalleOrdenDTO>) out.get("P_CURSOR");
    }

    @SuppressWarnings("unchecked")
    public List<ProductoProveedorDTO> listarProductosProveedor(Long proveedorId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PROVEEDOR_ID", proveedorId);

        Map<String, Object> out = spListarProductosProveedor.execute(params);
        return (List<ProductoProveedorDTO>) out.get("P_CURSOR");
    }

    public void eliminarOrdenCompra(Long ordenCompraId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ORDEN_COMPRA_ID", ordenCompraId);

        spEliminarOrden.execute(params);
    }

    public void actualizarDetalle(Long detalleOrdenId, Integer nuevaCantidad) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_DETALLE_ORDEN_ID", detalleOrdenId)
                .addValue("P_NUEVA_CANTIDAD", nuevaCantidad);

        spActualizarDetalle.execute(params);
    }

    public void eliminarDetalle(Long detalleOrdenId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_DETALLE_ORDEN_ID", detalleOrdenId);

        spEliminarDetalle.execute(params);
    }

    public void asociarProductoProveedor(Long proveedorId, Long productoId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PROVEEDOR_ID", proveedorId)
                .addValue("P_PRODUCTO_ID", productoId);

        spAsociarProductoProveedor.execute(params);
    }

    public List<ProductoDTO> listarProductosNoAsociadosProveedor(Long proveedorId) {
        String sql = "SELECT p.producto_id, p.nombre, p.categoria, p.descripcion, p.precio "
                + "FROM producto p "
                + "WHERE NOT EXISTS ( "
                + "   SELECT 1 FROM productoproveedor pp "
                + "   WHERE pp.producto_id = p.producto_id "
                + "     AND pp.proveedor_id = ? "
                + ") "
                + "ORDER BY p.nombre";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setProductoId(rs.getLong("PRODUCTO_ID"));
            dto.setNombre(rs.getString("NOMBRE"));
            dto.setCategoria(rs.getString("CATEGORIA"));
            dto.setDescripcion(rs.getString("DESCRIPCION"));
            dto.setPrecio(rs.getBigDecimal("PRECIO"));
            return dto;
        }, proveedorId);
    }

    public void eliminarAsociacionProductoProveedor(Long proveedorId, Long productoId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PROVEEDOR_ID", proveedorId)
                .addValue("P_PRODUCTO_ID", productoId);

        spEliminarAsociacionProductoProveedor.execute(params);
    }

}
