/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import com.proyecto.domain.ClienteDTO;
import java.sql.Types;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {

    private final JdbcTemplate jdbc;

    public ClienteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ClienteDTO> listar() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_CLIENTE")
                .withProcedureName("LISTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter("P_CURSOR", java.sql.Types.REF_CURSOR));

        Map<String, Object> out = call.execute(Collections.emptyMap());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");

        List<ClienteDTO> list = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> r : rows) {
                list.add(new ClienteDTO(
                        ((Number) r.get("CLIENTE_ID")).longValue(),
                        (String) r.get("NOMBRE"),
                        (String) r.get("CORREO"),
                        (String) r.get("TELEFONO"),
                        (String) r.get("DIRECCION")
                ));
            }
        }
        return list;
    }

    public Long crear(String nombre, String correo, String telefono, String direccion) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_CLIENTE")
                .withProcedureName("CREAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_NOMBRE", Types.VARCHAR),
                        new SqlParameter("P_CORREO", Types.VARCHAR),
                        new SqlParameter("P_TELEFONO", Types.VARCHAR),
                        new SqlParameter("P_DIRECCION", Types.VARCHAR),
                        new SqlOutParameter("P_CLIENTE_ID", Types.NUMERIC)
                );

        Map<String, Object> in = Map.of(
                "P_NOMBRE", nombre,
                "P_CORREO", correo,
                "P_TELEFONO", telefono,
                "P_DIRECCION", direccion
        );

        Map<String, Object> out = call.execute(in);
        return ((Number) out.get("P_CLIENTE_ID")).longValue();
    }

    public Optional<ClienteDTO> obtenerPorId(Long id) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_CLIENTE")
                .withProcedureName("OBTENER_POR_ID")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_CLIENTE_ID", Types.NUMERIC),
                        new SqlOutParameter("P_CURSOR", java.sql.Types.REF_CURSOR)
                );

        Map<String, Object> out = call.execute(Map.of("P_CLIENTE_ID", id));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");

        if (rows == null || rows.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> r = rows.get(0);
        return Optional.of(new ClienteDTO(
                ((Number) r.get("CLIENTE_ID")).longValue(),
                (String) r.get("NOMBRE"),
                (String) r.get("CORREO"),
                (String) r.get("TELEFONO"),
                (String) r.get("DIRECCION")
        ));
    }

    public void actualizar(Long id, String nombre, String correo, String telefono, String direccion) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_CLIENTE")
                .withProcedureName("ACTUALIZAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_CLIENTE_ID", Types.NUMERIC),
                        new SqlParameter("P_NOMBRE", Types.VARCHAR),
                        new SqlParameter("P_CORREO", Types.VARCHAR),
                        new SqlParameter("P_TELEFONO", Types.VARCHAR),
                        new SqlParameter("P_DIRECCION", Types.VARCHAR)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("P_CLIENTE_ID", id);
        in.put("P_NOMBRE", nombre);
        in.put("P_CORREO", correo);
        in.put("P_TELEFONO", telefono);
        in.put("P_DIRECCION", direccion);

        call.execute(in);
    }

    public void eliminar(Long id) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_CLIENTE")
                .withProcedureName("ELIMINAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("P_CLIENTE_ID", Types.NUMERIC));

        call.execute(Map.of("P_CLIENTE_ID", id));
    }
}
