CREATE OR REPLACE PACKAGE pkg_reclamos_app AS
    PROCEDURE sp_crear_atencion (
        p_cliente_id    IN atencioncliente.cliente_id%TYPE,
        p_pedido_id     IN atencioncliente.pedido_id%TYPE,
        p_tipo          IN atencioncliente.tipo%TYPE,
        p_descripcion   IN atencioncliente.descripcion%TYPE,
        p_estado        IN atencioncliente.estado%TYPE,
        p_atencion_id_o OUT atencioncliente.atencion_id%TYPE
    );

    PROCEDURE sp_actualizar_estado_atencion (
        p_atencion_id  IN atencioncliente.atencion_id%TYPE,
        p_nuevo_estado IN atencioncliente.estado%TYPE
    );

    PROCEDURE sp_listar_atenciones (
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE sp_crear_reclamo (
        p_atencion_id       IN reclamo.atencion_id%TYPE,
        p_detalle_pedido_id IN reclamo.detalle_pedido_id%TYPE,
        p_tipo_reclamo      IN reclamo.tipo_reclamo%TYPE,
        p_descripcion       IN reclamo.descripcion%TYPE,
        p_estado            IN reclamo.estado%TYPE,
        p_reclamo_id_o      OUT reclamo.reclamo_id%TYPE
    );

    PROCEDURE sp_actualizar_estado_reclamo (
        p_reclamo_id   IN reclamo.reclamo_id%TYPE,
        p_nuevo_estado IN reclamo.estado%TYPE
    );
    
    PROCEDURE sp_actualizar_reclamo (
        p_reclamo_id   IN reclamo.reclamo_id%TYPE,
        p_tipo_reclamo IN reclamo.tipo_reclamo%TYPE,
        p_descripcion  IN reclamo.descripcion%TYPE,
        p_estado       IN reclamo.estado%TYPE
    );

    PROCEDURE sp_eliminar_reclamo (
        p_reclamo_id IN reclamo.reclamo_id%TYPE
    );


    PROCEDURE sp_listar_reclamos (
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE sp_crear_devolucion (
        p_detalle_pedido_id IN devolucion.detalle_pedido_id%TYPE,
        p_reclamo_id        IN devolucion.reclamo_id%TYPE,
        p_motivo            IN devolucion.motivo%TYPE,
        p_estado            IN devolucion.estado%TYPE,
        p_devolucion_id_o   OUT devolucion.devolucion_id%TYPE
    );
    
    PROCEDURE sp_actualizar_devolucion (
        p_devolucion_id IN devolucion.devolucion_id%TYPE,
        p_motivo        IN devolucion.motivo%TYPE,
        p_estado        IN devolucion.estado%TYPE
    );

    PROCEDURE sp_eliminar_devolucion (
        p_devolucion_id IN devolucion.devolucion_id%TYPE
    );
    
    PROCEDURE sp_actualizar_atencion (
        p_atencion_id IN atencioncliente.atencion_id%TYPE,
        p_tipo        IN atencioncliente.tipo%TYPE,
        p_descripcion IN atencioncliente.descripcion%TYPE,
        p_estado      IN atencioncliente.estado%TYPE
);

    PROCEDURE sp_eliminar_atencion (
    p_atencion_id IN atencioncliente.atencion_id%TYPE
);



    PROCEDURE sp_listar_devoluciones (
        p_cursor OUT SYS_REFCURSOR
    );

    FUNCTION fn_atencion_existe (
        p_atencion_id IN atencioncliente.atencion_id%TYPE
    ) RETURN NUMBER;

    FUNCTION fn_reclamo_existe (
        p_reclamo_id IN reclamo.reclamo_id%TYPE
    ) RETURN NUMBER;

    FUNCTION fn_devolucion_existe (
        p_devolucion_id IN devolucion.devolucion_id%TYPE
    ) RETURN NUMBER;
    
    FUNCTION fn_total_reclamos_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) RETURN NUMBER;
    
    FUNCTION FN_TOTAL_DEVOLUCIONES_CLIENTE (
        p_cliente_id IN CLIENTE.CLIENTE_ID%TYPE
    ) RETURN NUMBER;


END pkg_reclamos_app;
/


CREATE OR REPLACE PACKAGE BODY pkg_reclamos_app AS

    PROCEDURE sp_crear_atencion (
        p_cliente_id    IN atencioncliente.cliente_id%TYPE,
        p_pedido_id     IN atencioncliente.pedido_id%TYPE,
        p_tipo          IN atencioncliente.tipo%TYPE,
        p_descripcion   IN atencioncliente.descripcion%TYPE,
        p_estado        IN atencioncliente.estado%TYPE,
        p_atencion_id_o OUT atencioncliente.atencion_id%TYPE
    ) AS
    BEGIN
        INSERT INTO atencioncliente (
            cliente_id,
            pedido_id,
            tipo,
            descripcion,
            fecha,
            estado
        ) VALUES ( p_cliente_id,
                   p_pedido_id,
                   p_tipo,
                   p_descripcion,
                   sysdate,
                   p_estado ) RETURNING atencion_id INTO p_atencion_id_o;

        COMMIT;
    END sp_crear_atencion;

    PROCEDURE sp_actualizar_estado_atencion (
        p_atencion_id  IN atencioncliente.atencion_id%TYPE,
        p_nuevo_estado IN atencioncliente.estado%TYPE
    ) AS
    BEGIN
        UPDATE atencioncliente
        SET
            estado = p_nuevo_estado
        WHERE
            atencion_id = p_atencion_id;

        COMMIT;
    END sp_actualizar_estado_atencion;

    PROCEDURE sp_listar_atenciones (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                a.atencion_id,
                                                a.cliente_id,
                                                c.nombre AS nombre_cliente,
                                                a.pedido_id,
                                                a.tipo,
                                                a.descripcion,
                                                a.fecha,
                                                a.estado
                                            FROM
                                                     atencioncliente a
                                                JOIN cliente c ON c.cliente_id = a.cliente_id
                          ORDER BY
                              a.fecha DESC;

    END sp_listar_atenciones;

    PROCEDURE sp_crear_reclamo (
        p_atencion_id       IN reclamo.atencion_id%TYPE,
        p_detalle_pedido_id IN reclamo.detalle_pedido_id%TYPE,
        p_tipo_reclamo      IN reclamo.tipo_reclamo%TYPE,
        p_descripcion       IN reclamo.descripcion%TYPE,
        p_estado            IN reclamo.estado%TYPE,
        p_reclamo_id_o      OUT reclamo.reclamo_id%TYPE
    ) AS
    BEGIN
        INSERT INTO reclamo (
            atencion_id,
            detalle_pedido_id,
            tipo_reclamo,
            descripcion,
            estado
        ) VALUES ( p_atencion_id,
                   p_detalle_pedido_id,
                   p_tipo_reclamo,
                   p_descripcion,
                   p_estado ) RETURNING reclamo_id INTO p_reclamo_id_o;

        COMMIT;
    END sp_crear_reclamo;

    PROCEDURE sp_actualizar_estado_reclamo (
        p_reclamo_id   IN reclamo.reclamo_id%TYPE,
        p_nuevo_estado IN reclamo.estado%TYPE
    ) AS
    BEGIN
        UPDATE reclamo
        SET
            estado = p_nuevo_estado
        WHERE
            reclamo_id = p_reclamo_id;

        COMMIT;
    END sp_actualizar_estado_reclamo;
    
    
    
        PROCEDURE sp_actualizar_reclamo (
        p_reclamo_id   IN reclamo.reclamo_id%TYPE,
        p_tipo_reclamo IN reclamo.tipo_reclamo%TYPE,
        p_descripcion  IN reclamo.descripcion%TYPE,
        p_estado       IN reclamo.estado%TYPE
    ) AS
    BEGIN
        UPDATE reclamo
        SET
            tipo_reclamo = p_tipo_reclamo,
            descripcion  = p_descripcion,
            estado       = p_estado
        WHERE
            reclamo_id = p_reclamo_id;

        COMMIT;
    END sp_actualizar_reclamo;

    PROCEDURE sp_eliminar_reclamo (
        p_reclamo_id IN reclamo.reclamo_id%TYPE
    ) AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM devolucion
        WHERE reclamo_id = p_reclamo_id;

        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(
                -20050,
                'No se puede eliminar el reclamo porque tiene devoluciones asociadas.'
            );
        END IF;

        DELETE FROM reclamo
        WHERE reclamo_id = p_reclamo_id;

        COMMIT;
    END sp_eliminar_reclamo;

    
    

    PROCEDURE sp_listar_reclamos (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                r.reclamo_id,
                                                r.atencion_id,
                                                a.cliente_id,
                                                c.nombre AS nombre_cliente,
                                                r.detalle_pedido_id,
                                                r.tipo_reclamo,
                                                r.descripcion,
                                                r.estado
                                            FROM
                                                     reclamo r
                                                JOIN atencioncliente a ON a.atencion_id = r.atencion_id
                                                JOIN cliente         c ON c.cliente_id = a.cliente_id
                          ORDER BY
                              r.reclamo_id DESC;

    END sp_listar_reclamos;

    PROCEDURE sp_crear_devolucion (
        p_detalle_pedido_id IN devolucion.detalle_pedido_id%TYPE,
        p_reclamo_id        IN devolucion.reclamo_id%TYPE,
        p_motivo            IN devolucion.motivo%TYPE,
        p_estado            IN devolucion.estado%TYPE,
        p_devolucion_id_o   OUT devolucion.devolucion_id%TYPE
    ) AS
    BEGIN
        INSERT INTO devolucion (
            detalle_pedido_id,
            reclamo_id,
            motivo,
            estado
        ) VALUES ( p_detalle_pedido_id,
                   p_reclamo_id,
                   p_motivo,
                   p_estado ) RETURNING devolucion_id INTO p_devolucion_id_o;

        COMMIT;
    END sp_crear_devolucion;

    PROCEDURE sp_listar_devoluciones (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                d.devolucion_id,
                                                d.detalle_pedido_id,
                                                d.reclamo_id,
                                                r.tipo_reclamo,
                                                d.motivo,
                                                d.estado
                                            FROM
                                                     devolucion d
                                                JOIN reclamo r ON r.reclamo_id = d.reclamo_id
                          ORDER BY
                              d.devolucion_id DESC;

    END sp_listar_devoluciones;
    
    PROCEDURE sp_actualizar_devolucion (
        p_devolucion_id IN devolucion.devolucion_id%TYPE,
        p_motivo        IN devolucion.motivo%TYPE,
        p_estado        IN devolucion.estado%TYPE
    ) AS
    BEGIN
        UPDATE devolucion
        SET motivo = p_motivo,
            estado = p_estado
        WHERE devolucion_id = p_devolucion_id;

        COMMIT;
    END sp_actualizar_devolucion;


    PROCEDURE sp_eliminar_devolucion (
        p_devolucion_id IN devolucion.devolucion_id%TYPE
    ) AS
    BEGIN
        DELETE FROM devolucion
        WHERE devolucion_id = p_devolucion_id;

        COMMIT;
    END sp_eliminar_devolucion;

    
    PROCEDURE sp_actualizar_atencion (
    p_atencion_id IN atencioncliente.atencion_id%TYPE,
    p_tipo        IN atencioncliente.tipo%TYPE,
    p_descripcion IN atencioncliente.descripcion%TYPE,
    p_estado      IN atencioncliente.estado%TYPE
) AS
BEGIN
    UPDATE atencioncliente
    SET tipo        = p_tipo,
        descripcion = p_descripcion,
        estado      = p_estado
    WHERE atencion_id = p_atencion_id;

    COMMIT;
END sp_actualizar_atencion;


    PROCEDURE sp_eliminar_atencion (
        p_atencion_id IN atencioncliente.atencion_id%TYPE
    ) AS
        v_count NUMBER;
        BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM reclamo
        WHERE atencion_id = p_atencion_id;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(
            -20060,
            'No se puede eliminar la atención porque tiene reclamos asociados.'
        );
    END IF;

        DELETE FROM atencioncliente
        WHERE atencion_id = p_atencion_id;

        COMMIT;
    END sp_eliminar_atencion;

    
    
   
    

    FUNCTION fn_atencion_existe (
        p_atencion_id IN atencioncliente.atencion_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            atencioncliente
        WHERE
            atencion_id = p_atencion_id;

        RETURN v_cant;
    END fn_atencion_existe;

    FUNCTION fn_reclamo_existe (
        p_reclamo_id IN reclamo.reclamo_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            reclamo
        WHERE
            reclamo_id = p_reclamo_id;

        RETURN v_cant;
    END fn_reclamo_existe;

    FUNCTION fn_devolucion_existe (
        p_devolucion_id IN devolucion.devolucion_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            devolucion
        WHERE
            devolucion_id = p_devolucion_id;

        RETURN v_cant;
    END fn_devolucion_existe;
    
    FUNCTION fn_total_reclamos_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) RETURN NUMBER AS
        v_total NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_total
        FROM
                 reclamo r
            JOIN atencioncliente a ON a.atencion_id = r.atencion_id
        WHERE
            a.cliente_id = p_cliente_id;

        RETURN v_total;
    END fn_total_reclamos_cliente;

    FUNCTION fn_total_devoluciones_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) RETURN NUMBER AS
        v_total NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_total
        FROM
                 devolucion d
            JOIN reclamo         r ON r.reclamo_id = d.reclamo_id
            JOIN atencioncliente a ON a.atencion_id = r.atencion_id
        WHERE
            a.cliente_id = p_cliente_id;

        RETURN v_total;
    END fn_total_devoluciones_cliente;



END pkg_reclamos_app;
/

