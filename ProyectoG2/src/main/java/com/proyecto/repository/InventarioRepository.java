/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import com.proyecto.domain.InventarioDTO;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class InventarioRepository {

    private final JdbcTemplate jdbc;

    public InventarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<InventarioDTO> listar() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_INVENTARIO")
                .withProcedureName("LISTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter("P_CURSOR", java.sql.Types.REF_CURSOR));

        Map<String, Object> out = call.execute(Collections.emptyMap());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");

        List<InventarioDTO> list = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> r : rows) {
                list.add(new InventarioDTO(
                        ((Number) r.get("PRODUCTO_ID")).longValue(),
                        (String) r.get("NOMBRE_PRODUCTO"),
                        ((Number) r.get("CANTIDAD_ACTUAL")).intValue()
                ));
            }
        }
        return list;

    }

    public Optional<InventarioDTO> obtenerPorId(Long productoId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_INVENTARIO")
                .withProcedureName("OBTENER_POR_ID")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                        new SqlOutParameter("P_CURSOR", java.sql.Types.REF_CURSOR)
                );

        Map<String, Object> out = call.execute(Map.of("P_PRODUCTO_ID", productoId));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");
        if (rows == null || rows.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> r = rows.get(0);
        return Optional.of(new InventarioDTO(
                ((Number) r.get("PRODUCTO_ID")).longValue(),
                (String) r.get("NOMBRE_PRODUCTO"),
                ((Number) r.get("CANTIDAD_ACTUAL")).intValue()
        ));
    }

    

    public void actualizar(Long productoId, Integer cantidad) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_INVENTARIO")
                .withProcedureName("ACTUALIZAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                        new SqlParameter("P_CANTIDAD", Types.NUMERIC)
                );

        call.execute(Map.of(
                "P_PRODUCTO_ID", productoId,
                "P_CANTIDAD", cantidad
        ));
    }

   
}
