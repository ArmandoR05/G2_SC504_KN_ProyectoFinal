CREATE OR REPLACE PACKAGE pkg_compras_app AS
    PROCEDURE sp_crear_proveedor (
        p_nombre         IN proveedor.nombre%TYPE,
        p_telefono       IN proveedor.telefono%TYPE,
        p_correo         IN proveedor.correo%TYPE,
        p_proveedor_id_o OUT proveedor.proveedor_id%TYPE
    );

    PROCEDURE sp_actualizar_proveedor (
        p_proveedor_id IN proveedor.proveedor_id%TYPE,
        p_nombre       IN proveedor.nombre%TYPE,
        p_telefono     IN proveedor.telefono%TYPE,
        p_correo       IN proveedor.correo%TYPE
    );
    
    PROCEDURE sp_eliminar_proveedor (
        p_proveedor_id IN proveedor.proveedor_id%TYPE
    );

    PROCEDURE sp_listar_proveedores (
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE sp_crear_orden_compra (
        p_proveedor_id      IN ordencompra.proveedor_id%TYPE,
        p_estado            IN ordencompra.estado%TYPE,
        p_orden_compra_id_o OUT ordencompra.orden_compra_id%TYPE
    );

    PROCEDURE sp_actualizar_estado_orden (
        p_orden_compra_id IN ordencompra.orden_compra_id%TYPE,
        p_nuevo_estado    IN ordencompra.estado%TYPE
    );

    PROCEDURE sp_listar_ordenes_compra (
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE sp_agregar_detalle_orden (
        p_orden_compra_id    IN detalleorden.orden_compra_id%TYPE,
        p_producto_id        IN detalleorden.producto_id%TYPE,
        p_cantidad           IN detalleorden.cantidad%TYPE,
        p_detalle_orden_id_o OUT detalleorden.detalle_orden_id%TYPE
    );
    
    PROCEDURE sp_eliminar_orden_compra (
        p_orden_compra_id IN ordencompra.orden_compra_id%TYPE
    );
    
    PROCEDURE sp_actualizar_detalle_orden (
        p_detalle_orden_id IN detalleorden.detalle_orden_id%TYPE,
        p_nueva_cantidad   IN detalleorden.cantidad%TYPE
    );

    PROCEDURE sp_eliminar_detalle_orden (
        p_detalle_orden_id IN detalleorden.detalle_orden_id%TYPE
    );


    PROCEDURE sp_listar_detalle_orden (
        p_orden_compra_id IN detalleorden.orden_compra_id%TYPE,
        p_cursor          OUT SYS_REFCURSOR
    );

    PROCEDURE sp_asociar_producto_proveedor (
        p_proveedor_id IN productoproveedor.proveedor_id%TYPE,
        p_producto_id  IN productoproveedor.producto_id%TYPE
    );
    
    PROCEDURE sp_eliminar_asociacion_producto_proveedor (
        p_proveedor_id IN productoproveedor.proveedor_id%TYPE,
        p_producto_id  IN productoproveedor.producto_id%TYPE
    );

    PROCEDURE sp_listar_productos_proveedor (
        p_proveedor_id IN productoproveedor.proveedor_id%TYPE,
        p_cursor       OUT SYS_REFCURSOR
    );

    FUNCTION fn_proveedor_existe (
        p_proveedor_id IN proveedor.proveedor_id%TYPE
    ) RETURN NUMBER;

    FUNCTION fn_orden_compra_existe (
        p_orden_compra_id IN ordencompra.orden_compra_id%TYPE
    ) RETURN NUMBER;
    
    FUNCTION fn_total_oc_proveedor (
        p_proveedor_id IN proveedor.proveedor_id%TYPE
    ) RETURN NUMBER;


END pkg_compras_app;
/





CREATE OR REPLACE PACKAGE BODY pkg_compras_app AS

    PROCEDURE sp_crear_proveedor (
        p_nombre         IN proveedor.nombre%TYPE,
        p_telefono       IN proveedor.telefono%TYPE,
        p_correo         IN proveedor.correo%TYPE,
        p_proveedor_id_o OUT proveedor.proveedor_id%TYPE
    ) AS
    BEGIN
        INSERT INTO proveedor (
            nombre,
            telefono,
            correo
        ) VALUES ( p_nombre,
                   p_telefono,
                   p_correo ) RETURNING proveedor_id INTO p_proveedor_id_o;

        COMMIT;
    END sp_crear_proveedor;

    PROCEDURE sp_actualizar_proveedor (
        p_proveedor_id IN proveedor.proveedor_id%TYPE,
        p_nombre       IN proveedor.nombre%TYPE,
        p_telefono     IN proveedor.telefono%TYPE,
        p_correo       IN proveedor.correo%TYPE
    ) AS
    BEGIN
        UPDATE proveedor
        SET
            nombre = p_nombre,
            telefono = p_telefono,
            correo = p_correo
        WHERE
            proveedor_id = p_proveedor_id;

        COMMIT;
    END sp_actualizar_proveedor;

    PROCEDURE sp_eliminar_proveedor (
        p_proveedor_id IN proveedor.proveedor_id%TYPE
    ) AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM ordencompra
        WHERE proveedor_id = p_proveedor_id;

        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20020,
                'No se puede eliminar el proveedor porque tiene órdenes de compra asociadas.');
        END IF;

        SELECT COUNT(*) INTO v_count
        FROM productoproveedor
        WHERE proveedor_id = p_proveedor_id;

        IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20021,
            'No se puede eliminar el proveedor porque está asociado a productos.');
        END IF;

        DELETE FROM proveedor
        WHERE proveedor_id = p_proveedor_id;

        COMMIT;
    END sp_eliminar_proveedor;

    PROCEDURE sp_eliminar_orden_compra (
    p_orden_compra_id IN ordencompra.orden_compra_id%TYPE
        ) AS
            v_count NUMBER;
        BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM detalleorden
    WHERE orden_compra_id = p_orden_compra_id;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(
            -20030,
            'No se puede eliminar la orden de compra porque tiene productos asociados.'
        );
    END IF;

    DELETE FROM ordencompra
    WHERE orden_compra_id = p_orden_compra_id;

    COMMIT;
    END sp_eliminar_orden_compra;


    PROCEDURE sp_actualizar_detalle_orden (
        p_detalle_orden_id IN detalleorden.detalle_orden_id%TYPE,
        p_nueva_cantidad   IN detalleorden.cantidad%TYPE
    ) AS
        v_producto_id   detalleorden.producto_id%TYPE;
        v_cant_actual   detalleorden.cantidad%TYPE;
        v_diferencia    NUMBER;
    BEGIN
        SELECT producto_id, cantidad
        INTO v_producto_id, v_cant_actual
        FROM detalleorden
        WHERE detalle_orden_id = p_detalle_orden_id;

        v_diferencia := p_nueva_cantidad - v_cant_actual;

        UPDATE inventario
        SET cantidad_actual = cantidad_actual + v_diferencia
        WHERE producto_id = v_producto_id;

        UPDATE detalleorden
        SET cantidad = p_nueva_cantidad
        WHERE detalle_orden_id = p_detalle_orden_id;

        COMMIT;
    END sp_actualizar_detalle_orden;

    PROCEDURE sp_eliminar_detalle_orden (
        p_detalle_orden_id IN detalleorden.detalle_orden_id%TYPE
    ) AS
        v_producto_id detalleorden.producto_id%TYPE;
        v_cantidad    detalleorden.cantidad%TYPE;
    BEGIN
        SELECT producto_id, cantidad
        INTO v_producto_id, v_cantidad
        FROM detalleorden
        WHERE detalle_orden_id = p_detalle_orden_id;

        UPDATE inventario
        SET cantidad_actual = cantidad_actual - v_cantidad
        WHERE producto_id = v_producto_id;

        DELETE FROM detalleorden
        WHERE detalle_orden_id = p_detalle_orden_id;

        COMMIT;
    END sp_eliminar_detalle_orden;




    PROCEDURE sp_listar_proveedores (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                proveedor_id,
                                                nombre,
                                                telefono,
                                                correo
                                            FROM
                                                proveedor
                          ORDER BY
                              nombre;

    END sp_listar_proveedores;

    PROCEDURE sp_crear_orden_compra (
        p_proveedor_id      IN ordencompra.proveedor_id%TYPE,
        p_estado            IN ordencompra.estado%TYPE,
        p_orden_compra_id_o OUT ordencompra.orden_compra_id%TYPE
    ) AS
    BEGIN
        INSERT INTO ordencompra (
            proveedor_id,
            fecha,
            estado
        ) VALUES ( p_proveedor_id,
                   sysdate,
                   p_estado ) RETURNING orden_compra_id INTO p_orden_compra_id_o;

        COMMIT;
    END sp_crear_orden_compra;

    PROCEDURE sp_actualizar_estado_orden (
        p_orden_compra_id IN ordencompra.orden_compra_id%TYPE,
        p_nuevo_estado    IN ordencompra.estado%TYPE
    ) AS
    BEGIN
        UPDATE ordencompra
        SET
            estado = p_nuevo_estado
        WHERE
            orden_compra_id = p_orden_compra_id;

        COMMIT;
    END sp_actualizar_estado_orden;

    PROCEDURE sp_listar_ordenes_compra (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                o.orden_compra_id,
                                                o.proveedor_id,
                                                p.nombre AS nombre_proveedor,
                                                o.fecha,
                                                o.estado
                                            FROM
                                                     ordencompra o
                                                JOIN proveedor p ON p.proveedor_id = o.proveedor_id
                          ORDER BY
                              o.fecha DESC;

    END sp_listar_ordenes_compra;

    PROCEDURE sp_agregar_detalle_orden (
        p_orden_compra_id    IN detalleorden.orden_compra_id%TYPE,
        p_producto_id        IN detalleorden.producto_id%TYPE,
        p_cantidad           IN detalleorden.cantidad%TYPE,
        p_detalle_orden_id_o OUT detalleorden.detalle_orden_id%TYPE
    ) AS
    BEGIN
        INSERT INTO detalleorden (
            orden_compra_id,
            producto_id,
            cantidad
        ) VALUES ( p_orden_compra_id,
                   p_producto_id,
                   p_cantidad ) RETURNING detalle_orden_id INTO p_detalle_orden_id_o;

        COMMIT;
    END sp_agregar_detalle_orden;

    PROCEDURE sp_listar_detalle_orden (
        p_orden_compra_id IN detalleorden.orden_compra_id%TYPE,
        p_cursor          OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                d.detalle_orden_id,
                                                d.orden_compra_id,
                                                d.producto_id,
                                                p.nombre AS nombre_producto,
                                                d.cantidad
                                            FROM
                                                     detalleorden d
                                                JOIN producto p ON p.producto_id = d.producto_id
                          WHERE
                              d.orden_compra_id = p_orden_compra_id
                          ORDER BY
                              d.detalle_orden_id;

    END sp_listar_detalle_orden;

    PROCEDURE sp_asociar_producto_proveedor (
        p_proveedor_id IN productoproveedor.proveedor_id%TYPE,
        p_producto_id  IN productoproveedor.producto_id%TYPE
    ) AS
    BEGIN
        INSERT INTO productoproveedor (
            proveedor_id,
            producto_id
        ) VALUES ( p_proveedor_id,
                   p_producto_id );

        COMMIT;
    END sp_asociar_producto_proveedor;

    PROCEDURE sp_listar_productos_proveedor (
        p_proveedor_id IN productoproveedor.proveedor_id%TYPE,
        p_cursor       OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                pp.proveedor_id,
                                                pr.nombre AS nombre_proveedor,
                                                pp.producto_id,
                                                p.nombre  AS nombre_producto
                                            FROM
                                                     productoproveedor pp
                                                JOIN proveedor pr ON pr.proveedor_id = pp.proveedor_id
                                                JOIN producto  p ON p.producto_id = pp.producto_id
                          WHERE
                              pp.proveedor_id = p_proveedor_id
                          ORDER BY
                              p.nombre;

    END sp_listar_productos_proveedor;
    
    PROCEDURE sp_eliminar_asociacion_producto_proveedor (
        p_proveedor_id IN productoproveedor.proveedor_id%TYPE,
        p_producto_id  IN productoproveedor.producto_id%TYPE
    ) AS
    BEGIN
        DELETE FROM productoproveedor
        WHERE
                proveedor_id = p_proveedor_id
            AND producto_id = p_producto_id;

        COMMIT;
    END sp_eliminar_asociacion_producto_proveedor;
    

    FUNCTION fn_proveedor_existe (
        p_proveedor_id IN proveedor.proveedor_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            proveedor
        WHERE
            proveedor_id = p_proveedor_id;

        RETURN v_cant;
    END fn_proveedor_existe;

    FUNCTION fn_orden_compra_existe (
        p_orden_compra_id IN ordencompra.orden_compra_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            ordencompra
        WHERE
            orden_compra_id = p_orden_compra_id;

        RETURN v_cant;
    END fn_orden_compra_existe;
    
       FUNCTION fn_total_oc_proveedor (
        p_proveedor_id IN proveedor.proveedor_id%TYPE
    ) RETURN NUMBER AS
        v_total NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_total
        FROM
            ordencompra
        WHERE
            proveedor_id = p_proveedor_id;

        RETURN v_total;
    END fn_total_oc_proveedor;


END pkg_compras_app;
/

