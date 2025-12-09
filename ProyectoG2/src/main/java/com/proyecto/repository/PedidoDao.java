/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;


import com.proyecto.domain.DetallePedidoDTO;
import com.proyecto.domain.PedidoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class PedidoDao {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall spCrearPedido;
    private final SimpleJdbcCall spActualizarEstado;
    private final SimpleJdbcCall spEliminarPedido;
    private final SimpleJdbcCall spObtenerPedido;
    private final SimpleJdbcCall spListarPedidos;
    private final SimpleJdbcCall spAgregarDetalle;
    private final SimpleJdbcCall spListarDetalle;

    public PedidoDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.spCrearPedido = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_CREAR_PEDIDO");

        this.spActualizarEstado = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_ACTUALIZAR_ESTADO_PEDIDO");

        this.spEliminarPedido = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_ELIMINAR_PEDIDO");

        this.spObtenerPedido = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_OBTENER_PEDIDO");

        this.spListarPedidos = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_LISTAR_PEDIDOS")
                .returningResultSet("P_CURSOR", new PedidoRowMapper());

        this.spAgregarDetalle = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_AGREGAR_DETALLE_PEDIDO");

        this.spListarDetalle = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_PEDIDO_APP")
                .withProcedureName("SP_LISTAR_DETALLE_PEDIDO")
                .returningResultSet("P_CURSOR", new DetalleRowMapper());
    }


    private static class PedidoRowMapper implements RowMapper<PedidoDTO> {
        @Override
        public PedidoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            PedidoDTO dto = new PedidoDTO();
            dto.setPedidoId(rs.getLong("PEDIDO_ID"));
            dto.setClienteId(rs.getLong("CLIENTE_ID"));
            dto.setNombreCliente(rs.getString("NOMBRE_CLIENTE"));
            dto.setFecha(rs.getTimestamp("FECHA"));
            dto.setEstado(rs.getString("ESTADO"));
            dto.setTotalPedido(rs.getBigDecimal("TOTAL_PEDIDO"));
            return dto;
        }
    }

    private static class DetalleRowMapper implements RowMapper<DetallePedidoDTO> {
        @Override
        public DetallePedidoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            DetallePedidoDTO dto = new DetallePedidoDTO();
            dto.setDetallePedidoId(rs.getLong("DETALLE_PEDIDO_ID"));
            dto.setPedidoId(rs.getLong("PEDIDO_ID"));
            dto.setProductoId(rs.getLong("PRODUCTO_ID"));
            dto.setNombreProducto(rs.getString("NOMBRE_PRODUCTO"));
            dto.setCantidad(rs.getInt("CANTIDAD"));
            dto.setSubtotal(rs.getBigDecimal("SUBTOTAL"));
            return dto;
        }
    }


    public Long crearPedido(Long clienteId, String estado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_CLIENTE_ID", clienteId)
                .addValue("P_ESTADO", estado);

        Map<String, Object> out = spCrearPedido.execute(params);
        Object idOut = out.get("P_PEDIDO_ID_O");
        if (idOut == null) return null;
        return ((Number) idOut).longValue();
    }

    public void actualizarEstado(Long pedidoId, String nuevoEstado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PEDIDO_ID", pedidoId)
                .addValue("P_NUEVO_ESTADO", nuevoEstado);
        spActualizarEstado.execute(params);
    }

    public void eliminarPedido(Long pedidoId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PEDIDO_ID", pedidoId);
        spEliminarPedido.execute(params);
    }

    public PedidoDTO obtenerPedido(Long pedidoId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PEDIDO_ID", pedidoId);

        Map<String, Object> out = spObtenerPedido.execute(params);

        PedidoDTO dto = new PedidoDTO();
        dto.setPedidoId(pedidoId);
        dto.setClienteId(((Number) out.get("P_CLIENTE_ID_O")).longValue());
        dto.setFecha((java.util.Date) out.get("P_FECHA_O"));
        dto.setEstado((String) out.get("P_ESTADO_O"));

        dto.setTotalPedido(obtenerTotalPedido(pedidoId));
        return dto;
    }

    @SuppressWarnings("unchecked")
    public List<PedidoDTO> listarPedidos() {
        Map<String, Object> out = spListarPedidos.execute();
        return (List<PedidoDTO>) out.get("P_CURSOR");
    }

    public BigDecimal obtenerTotalPedido(Long pedidoId) {
        String sql = "SELECT PKG_PEDIDO_APP.FN_TOTAL_PEDIDO(?) FROM DUAL";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, pedidoId);
    }


    public void agregarDetalle(Long pedidoId, Long productoId, Integer cantidad) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PEDIDO_ID", pedidoId)
                .addValue("P_PRODUCTO_ID", productoId)
                .addValue("P_CANTIDAD", cantidad);

        spAgregarDetalle.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<DetallePedidoDTO> listarDetalle(Long pedidoId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PEDIDO_ID", pedidoId);

        Map<String, Object> out = spListarDetalle.execute(params);
        return (List<DetallePedidoDTO>) out.get("P_CURSOR");
    }
}

