/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.repository;

import com.proyecto.domain.ProductoDTO;
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

/**
 *
 * @author PC
 */
@Repository
public class ProductoRepository {

    private final JdbcTemplate jdbc;

    public ProductoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ProductoDTO> listar() {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("LISTAR")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter("P_CURSOR", java.sql.Types.REF_CURSOR));

        Map<String, Object> out = call.execute(Collections.emptyMap());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");

        List<ProductoDTO> list = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> r : rows) {
                list.add(new ProductoDTO(
                        ((Number) r.get("PRODUCTO_ID")).longValue(),
                        (String) r.get("NOMBRE"),
                        (String) r.get("CATEGORIA"),
                        (String) r.get("DESCRIPCION"),
                        r.get("PRECIO") == null ? null : ((Number) r.get("PRECIO")).doubleValue(),
                        r.get("CANTIDAD_ACTUAL") == null ? 0 : ((Number) r.get("CANTIDAD_ACTUAL")).intValue()
                ));
            }
        }
        return list;
    }

    public Long crear(String nombre, String categoria, String descripcion, Double precio) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("CREAR")
                .declareParameters(
                        new SqlParameter("P_NOMBRE", Types.NVARCHAR),
                        new SqlParameter("P_CATEGORIA", Types.NVARCHAR),
                        new SqlParameter("P_DESCRIPCION", Types.NVARCHAR),
                        new SqlParameter("P_PRECIO", Types.NUMERIC),
                        new SqlOutParameter("P_PRODUCTO_ID", Types.NUMERIC)
                );

        Map<String, Object> in = Map.of(
                "P_NOMBRE", nombre,
                "P_CATEGORIA", categoria,
                "P_DESCRIPCION", descripcion,
                "P_PRECIO", precio
        );

        Map<String, Object> out = call.execute(in);
        return ((Number) out.get("P_PRODUCTO_ID")).longValue();
    }

    public void actualizar(Long id, String nombre, String categoria, String descripcion, Double precio) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR") 
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("ACTUALIZAR")
                .withoutProcedureColumnMetaDataAccess() 
                .declareParameters(
                        new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                        new SqlParameter("P_NOMBRE", Types.NVARCHAR),
                        new SqlParameter("P_CATEGORIA", Types.NVARCHAR),
                        new SqlParameter("P_DESCRIPCION", Types.NVARCHAR),
                        new SqlParameter("P_PRECIO", Types.NUMERIC)
                );

        Map<String, Object> in = new HashMap<>();
        in.put("P_PRODUCTO_ID", id);
        in.put("P_NOMBRE", nombre);
        in.put("P_CATEGORIA", categoria);
        in.put("P_DESCRIPCION", descripcion);
        in.put("P_PRECIO", precio);

        call.execute(in);
    }

    public void eliminar(Long id) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("ELIMINAR");

        call.execute(Map.of("P_PRODUCTO_ID", id));
    }

    public Optional<ProductoDTO> obtenerPorId(Long id) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc)
                .withSchemaName("DESARROLLADOR")
                .withCatalogName("PKG_PRODUCTO")
                .withProcedureName("OBTENER_POR_ID")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_PRODUCTO_ID", Types.NUMERIC),
                        new SqlOutParameter("P_CURSOR", java.sql.Types.REF_CURSOR)
                );

        Map<String, Object> out = call.execute(Map.of("P_PRODUCTO_ID", id));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) out.get("P_CURSOR");
        if (rows == null || rows.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> r = rows.get(0);
        return Optional.of(new ProductoDTO(
                ((Number) r.get("PRODUCTO_ID")).longValue(),
                (String) r.get("NOMBRE"),
                (String) r.get("CATEGORIA"),
                (String) r.get("DESCRIPCION"),
                r.get("PRECIO") == null ? null : ((Number) r.get("PRECIO")).doubleValue(),
                r.get("CANTIDAD_ACTUAL") == null ? 0 : ((Number) r.get("CANTIDAD_ACTUAL")).intValue()
        ));
    }

}
