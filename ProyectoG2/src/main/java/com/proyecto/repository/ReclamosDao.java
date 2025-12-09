/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import com.proyecto.domain.AtencionDTO;
import com.proyecto.domain.ReclamoDTO;
import com.proyecto.domain.DevolucionDTO;
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
public class ReclamosDao {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall spCrearAtencion;
    private final SimpleJdbcCall spActualizarEstadoAtencion;
    private final SimpleJdbcCall spListarAtenciones;
    private final SimpleJdbcCall spActualizarAtencion;
    private final SimpleJdbcCall spEliminarAtencion;

    private final SimpleJdbcCall spCrearReclamo;
    private final SimpleJdbcCall spActualizarEstadoReclamo;
    private final SimpleJdbcCall spListarReclamos;
    private final SimpleJdbcCall spActualizarReclamo;
    private final SimpleJdbcCall spEliminarReclamo;

    private final SimpleJdbcCall spCrearDevolucion;
    private final SimpleJdbcCall spListarDevoluciones;
    private final SimpleJdbcCall spActualizarDevolucion;
    private final SimpleJdbcCall spEliminarDevolucion;

    public ReclamosDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.spCrearAtencion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_CREAR_ATENCION");

        this.spActualizarEstadoAtencion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ACTUALIZAR_ESTADO_ATENCION");

        this.spListarAtenciones = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_LISTAR_ATENCIONES")
                .returningResultSet("P_CURSOR", new AtencionRowMapper());

        this.spActualizarAtencion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ACTUALIZAR_ATENCION");

        this.spEliminarAtencion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ELIMINAR_ATENCION");

        this.spCrearReclamo = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_CREAR_RECLAMO");

        this.spActualizarEstadoReclamo = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ACTUALIZAR_ESTADO_RECLAMO");

        this.spListarReclamos = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_LISTAR_RECLAMOS")
                .returningResultSet("P_CURSOR", new ReclamoRowMapper());

        this.spCrearDevolucion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_CREAR_DEVOLUCION");

        this.spListarDevoluciones = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_LISTAR_DEVOLUCIONES")
                .returningResultSet("P_CURSOR", new DevolucionRowMapper());
        this.spActualizarReclamo = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ACTUALIZAR_RECLAMO");

        this.spEliminarReclamo = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ELIMINAR_RECLAMO");
        this.spActualizarDevolucion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ACTUALIZAR_DEVOLUCION");

        this.spEliminarDevolucion = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RECLAMOS_APP")
                .withProcedureName("SP_ELIMINAR_DEVOLUCION");

    }

    private static class AtencionRowMapper implements RowMapper<AtencionDTO> {

        @Override
        public AtencionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AtencionDTO dto = new AtencionDTO();
            dto.setAtencionId(rs.getLong("ATENCION_ID"));
            dto.setClienteId(rs.getLong("CLIENTE_ID"));
            dto.setNombreCliente(rs.getString("NOMBRE_CLIENTE"));
            dto.setPedidoId(rs.getLong("PEDIDO_ID"));
            dto.setTipo(rs.getString("TIPO"));
            dto.setDescripcion(rs.getString("DESCRIPCION"));
            dto.setFecha(rs.getTimestamp("FECHA"));
            dto.setEstado(rs.getString("ESTADO"));
            return dto;
        }
    }

    private static class ReclamoRowMapper implements RowMapper<ReclamoDTO> {

        @Override
        public ReclamoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReclamoDTO dto = new ReclamoDTO();
            dto.setReclamoId(rs.getLong("RECLAMO_ID"));
            dto.setAtencionId(rs.getLong("ATENCION_ID"));
            dto.setClienteId(rs.getLong("CLIENTE_ID"));
            dto.setNombreCliente(rs.getString("NOMBRE_CLIENTE"));
            dto.setDetallePedidoId(rs.getLong("DETALLE_PEDIDO_ID"));
            dto.setTipoReclamo(rs.getString("TIPO_RECLAMO"));
            dto.setDescripcion(rs.getString("DESCRIPCION"));
            dto.setEstado(rs.getString("ESTADO"));
            return dto;
        }
    }

    private static class DevolucionRowMapper implements RowMapper<DevolucionDTO> {

        @Override
        public DevolucionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            DevolucionDTO dto = new DevolucionDTO();
            dto.setDevolucionId(rs.getLong("DEVOLUCION_ID"));
            dto.setDetallePedidoId(rs.getLong("DETALLE_PEDIDO_ID"));
            dto.setReclamoId(rs.getLong("RECLAMO_ID"));
            dto.setTipoReclamo(rs.getString("TIPO_RECLAMO"));
            dto.setMotivo(rs.getString("MOTIVO"));
            dto.setEstado(rs.getString("ESTADO"));
            return dto;
        }
    }

    public Long crearAtencion(Long clienteId,
            Long pedidoId,
            String tipo,
            String descripcion,
            String estado) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_CLIENTE_ID", clienteId)
                .addValue("P_PEDIDO_ID", pedidoId)
                .addValue("P_TIPO", tipo)
                .addValue("P_DESCRIPCION", descripcion)
                .addValue("P_ESTADO", estado);

        Map<String, Object> out = spCrearAtencion.execute(params);
        Object idOut = out.get("P_ATENCION_ID_O");
        return idOut == null ? null : ((Number) idOut).longValue();
    }

    public void actualizarEstadoAtencion(Long atencionId, String nuevoEstado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ATENCION_ID", atencionId)
                .addValue("P_NUEVO_ESTADO", nuevoEstado);

        spActualizarEstadoAtencion.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<AtencionDTO> listarAtenciones() {
        Map<String, Object> out = spListarAtenciones.execute();
        return (List<AtencionDTO>) out.get("P_CURSOR");
    }

    public Long crearReclamo(Long atencionId,
            Long detallePedidoId,
            String tipoReclamo,
            String descripcion,
            String estado) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ATENCION_ID", atencionId)
                .addValue("P_DETALLE_PEDIDO_ID", detallePedidoId)
                .addValue("P_TIPO_RECLAMO", tipoReclamo)
                .addValue("P_DESCRIPCION", descripcion)
                .addValue("P_ESTADO", estado);

        Map<String, Object> out = spCrearReclamo.execute(params);
        Object idOut = out.get("P_RECLAMO_ID_O");
        return idOut == null ? null : ((Number) idOut).longValue();
    }

    public void actualizarEstadoReclamo(Long reclamoId, String nuevoEstado) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_RECLAMO_ID", reclamoId)
                .addValue("P_NUEVO_ESTADO", nuevoEstado);

        spActualizarEstadoReclamo.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<ReclamoDTO> listarReclamos() {
        Map<String, Object> out = spListarReclamos.execute();
        return (List<ReclamoDTO>) out.get("P_CURSOR");
    }

    public Long crearDevolucion(Long detallePedidoId,
            Long reclamoId,
            String motivo,
            String estado) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_DETALLE_PEDIDO_ID", detallePedidoId)
                .addValue("P_RECLAMO_ID", reclamoId)
                .addValue("P_MOTIVO", motivo)
                .addValue("P_ESTADO", estado);

        Map<String, Object> out = spCrearDevolucion.execute(params);
        Object idOut = out.get("P_DEVOLUCION_ID_O");
        return idOut == null ? null : ((Number) idOut).longValue();
    }

    @SuppressWarnings("unchecked")
    public List<DevolucionDTO> listarDevoluciones() {
        Map<String, Object> out = spListarDevoluciones.execute();
        return (List<DevolucionDTO>) out.get("P_CURSOR");
    }

    public ReclamoDTO obtenerReclamo(Long reclamoId) {
        String sql = "SELECT r.reclamo_id, r.atencion_id, a.cliente_id, c.nombre AS nombre_cliente, "
                + "       r.detalle_pedido_id, r.tipo_reclamo, r.descripcion, r.estado "
                + "FROM   reclamo r "
                + "JOIN   atencioncliente a ON a.atencion_id = r.atencion_id "
                + "JOIN   cliente c ON c.cliente_id = a.cliente_id "
                + "WHERE  r.reclamo_id = ?";

        return jdbcTemplate.queryForObject(sql, new ReclamoRowMapper(), reclamoId);
    }

    public void actualizarReclamo(Long reclamoId,
            String tipoReclamo,
            String descripcion,
            String estado) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_RECLAMO_ID", reclamoId)
                .addValue("P_TIPO_RECLAMO", tipoReclamo)
                .addValue("P_DESCRIPCION", descripcion)
                .addValue("P_ESTADO", estado);

        spActualizarReclamo.execute(params);
    }

    public void eliminarReclamo(Long reclamoId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_RECLAMO_ID", reclamoId);

        spEliminarReclamo.execute(params);
    }

    public DevolucionDTO obtenerDevolucion(Long devolucionId) {
        String sql = "SELECT d.devolucion_id, d.detalle_pedido_id, d.reclamo_id, "
                + "       r.tipo_reclamo, d.motivo, d.estado "
                + "FROM   devolucion d "
                + "JOIN   reclamo r ON r.reclamo_id = d.reclamo_id "
                + "WHERE  d.devolucion_id = ?";

        return jdbcTemplate.queryForObject(sql, new DevolucionRowMapper(), devolucionId);
    }

    public void actualizarDevolucion(Long devolucionId,
            String motivo,
            String estado) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_DEVOLUCION_ID", devolucionId)
                .addValue("P_MOTIVO", motivo)
                .addValue("P_ESTADO", estado);

        spActualizarDevolucion.execute(params);
    }

    public void eliminarDevolucion(Long devolucionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_DEVOLUCION_ID", devolucionId);

        spEliminarDevolucion.execute(params);
    }

    public AtencionDTO obtenerAtencion(Long atencionId) {
        String sql = "SELECT a.atencion_id, a.cliente_id, c.nombre AS nombre_cliente, "
                + "       a.pedido_id, a.tipo, a.descripcion, a.fecha, a.estado "
                + "FROM   atencioncliente a "
                + "JOIN   cliente c ON c.cliente_id = a.cliente_id "
                + "WHERE  a.atencion_id = ?";

        return jdbcTemplate.queryForObject(sql, new AtencionRowMapper(), atencionId);
    }

    public void actualizarAtencion(Long atencionId,
            String tipo,
            String descripcion,
            String estado) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ATENCION_ID", atencionId)
                .addValue("P_TIPO", tipo)
                .addValue("P_DESCRIPCION", descripcion)
                .addValue("P_ESTADO", estado);

        spActualizarAtencion.execute(params);
    }

    public void eliminarAtencion(Long atencionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_ATENCION_ID", atencionId);

        spEliminarAtencion.execute(params);
    }

}
