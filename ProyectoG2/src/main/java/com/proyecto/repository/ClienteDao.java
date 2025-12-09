/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;


import com.proyecto.domain.ClienteDTO;
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
public class ClienteDao {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall spCrearCliente;
    private final SimpleJdbcCall spActualizarCliente;
    private final SimpleJdbcCall spEliminarCliente;
    private final SimpleJdbcCall spObtenerCliente;
    private final SimpleJdbcCall spListarClientes;

    public ClienteDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.spCrearCliente = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_CLIENTE_APP")
                .withProcedureName("SP_CREAR_CLIENTE");

        this.spActualizarCliente = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_CLIENTE_APP")
                .withProcedureName("SP_ACTUALIZAR_CLIENTE");

        this.spEliminarCliente = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_CLIENTE_APP")
                .withProcedureName("SP_ELIMINAR_CLIENTE");

        this.spObtenerCliente = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_CLIENTE_APP")
                .withProcedureName("SP_OBTENER_CLIENTE");

        this.spListarClientes = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_CLIENTE_APP")
                .withProcedureName("SP_LISTAR_CLIENTES")
                .returningResultSet("P_CURSOR", new ClienteRowMapper());
    }

    private static class ClienteRowMapper implements RowMapper<ClienteDTO> {
        @Override
        public ClienteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ClienteDTO dto = new ClienteDTO();
            dto.setClienteId(rs.getLong("CLIENTE_ID"));
            dto.setNombre(rs.getString("NOMBRE"));
            dto.setCorreo(rs.getString("CORREO"));
            dto.setTelefono(rs.getString("TELEFONO"));
            dto.setDireccion(rs.getString("DIRECCION"));
            return dto;
        }
    }

    public Long crearCliente(ClienteDTO dto) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_NOMBRE", dto.getNombre())
                .addValue("P_CORREO", dto.getCorreo())
                .addValue("P_TELEFONO", dto.getTelefono())
                .addValue("P_DIRECCION", dto.getDireccion());

        Map<String, Object> out = spCrearCliente.execute(params);

        Object idOut = out.get("P_CLIENTE_ID_O");
        if (idOut == null) {
            return null;
        }
        return ((Number) idOut).longValue();
    }

    public void actualizarCliente(ClienteDTO dto) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_CLIENTE_ID", dto.getClienteId())
                .addValue("P_NOMBRE", dto.getNombre())
                .addValue("P_CORREO", dto.getCorreo())
                .addValue("P_TELEFONO", dto.getTelefono())
                .addValue("P_DIRECCION", dto.getDireccion());

        spActualizarCliente.execute(params);
    }

    public void eliminarCliente(Long clienteId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_CLIENTE_ID", clienteId);

        spEliminarCliente.execute(params);
    }

    public ClienteDTO obtenerCliente(Long clienteId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("P_CLIENTE_ID", clienteId);

        Map<String, Object> out = spObtenerCliente.execute(params);

        ClienteDTO dto = new ClienteDTO();
        dto.setClienteId(clienteId);
        dto.setNombre((String) out.get("P_NOMBRE"));
        dto.setCorreo((String) out.get("P_CORREO"));
        dto.setTelefono((String) out.get("P_TELEFONO"));
        dto.setDireccion((String) out.get("P_DIRECCION"));

        return dto;
    }

    @SuppressWarnings("unchecked")
    public List<ClienteDTO> listarClientes() {
        Map<String, Object> out = spListarClientes.execute();
        return (List<ClienteDTO>) out.get("P_CURSOR");
    }
}

