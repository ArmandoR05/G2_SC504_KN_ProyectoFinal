/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;


import com.proyecto.domain.ProveedorDTO;
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
public class ProveedorDao {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall spCrearProveedor;
    private final SimpleJdbcCall spActualizarProveedor;
    private final SimpleJdbcCall spListarProveedores;
    private final SimpleJdbcCall spEliminarProveedor;


    public ProveedorDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.spCrearProveedor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_CREAR_PROVEEDOR");

        this.spActualizarProveedor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ACTUALIZAR_PROVEEDOR");

        this.spListarProveedores = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_LISTAR_PROVEEDORES")
                .returningResultSet("P_CURSOR", new ProveedorRowMapper());
        this.spEliminarProveedor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_COMPRAS_APP")
                .withProcedureName("SP_ELIMINAR_PROVEEDOR");

    }

    private static class ProveedorRowMapper implements RowMapper<ProveedorDTO> {
        @Override
        public ProveedorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProveedorDTO dto = new ProveedorDTO();
            dto.setProveedorId(rs.getLong("PROVEEDOR_ID"));
            dto.setNombre(rs.getString("NOMBRE"));
            dto.setTelefono(rs.getString("TELEFONO"));
            dto.setCorreo(rs.getString("CORREO"));
            return dto;
        }
    }

    public Long crearProveedor(String nombre, String telefono, String correo) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_NOMBRE", nombre)
                .addValue("P_TELEFONO", telefono)
                .addValue("P_CORREO", correo);

        Map<String, Object> out = spCrearProveedor.execute(params);
        Object idOut = out.get("P_PROVEEDOR_ID_O");
        if (idOut == null) return null;
        return ((Number) idOut).longValue();
    }

    public void actualizarProveedor(Long proveedorId, String nombre, String telefono, String correo) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_PROVEEDOR_ID", proveedorId)
                .addValue("P_NOMBRE", nombre)
                .addValue("P_TELEFONO", telefono)
                .addValue("P_CORREO", correo);

        spActualizarProveedor.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<ProveedorDTO> listarProveedores() {
        Map<String, Object> out = spListarProveedores.execute();
        return (List<ProveedorDTO>) out.get("P_CURSOR");
    }

    public ProveedorDTO obtenerProveedor(Long proveedorId) {
        String sql = "SELECT PROVEEDOR_ID, NOMBRE, TELEFONO, CORREO FROM PROVEEDOR WHERE PROVEEDOR_ID = ?";
        return jdbcTemplate.queryForObject(sql, new ProveedorRowMapper(), proveedorId);
    }
    
    public void eliminarProveedor(Long proveedorId) {
    MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("P_PROVEEDOR_ID", proveedorId);

    spEliminarProveedor.execute(params);
}

    
    
    
}

