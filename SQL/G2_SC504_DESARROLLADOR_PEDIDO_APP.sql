CREATE OR REPLACE PACKAGE pkg_pedido_app AS
    PROCEDURE sp_crear_pedido (
        p_cliente_id  IN pedido.cliente_id%TYPE,
        p_estado      IN pedido.estado%TYPE,
        p_pedido_id_o OUT pedido.pedido_id%TYPE
    );

    PROCEDURE sp_actualizar_estado_pedido (
        p_pedido_id    IN pedido.pedido_id%TYPE,
        p_nuevo_estado IN pedido.estado%TYPE
    );

    PROCEDURE sp_eliminar_pedido (
        p_pedido_id IN pedido.pedido_id%TYPE
    );

    PROCEDURE sp_obtener_pedido (
        p_pedido_id    IN pedido.pedido_id%TYPE,
        p_cliente_id_o OUT pedido.cliente_id%TYPE,
        p_fecha_o      OUT pedido.fecha%TYPE,
        p_estado_o     OUT pedido.estado%TYPE
    );

    PROCEDURE sp_listar_pedidos (
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE sp_agregar_detalle_pedido (
        p_pedido_id    IN detallepedido.pedido_id%TYPE,
        p_producto_id  IN detallepedido.producto_id%TYPE,
        p_cantidad     IN detallepedido.cantidad%TYPE,
        p_detalle_id_o OUT detallepedido.detalle_pedido_id%TYPE
    );

    PROCEDURE sp_listar_detalle_pedido (
        p_pedido_id IN detallepedido.pedido_id%TYPE,
        p_cursor    OUT SYS_REFCURSOR
    );

    PROCEDURE sp_actualizar_detalle_pedido (
        p_detalle_id  IN detallepedido.detalle_pedido_id%TYPE,
        p_producto_id IN detallepedido.producto_id%TYPE,
        p_cantidad    IN detallepedido.cantidad%TYPE
    );

    PROCEDURE sp_eliminar_detalle_pedido (
        p_detalle_id IN detallepedido.detalle_pedido_id%TYPE
    );

    FUNCTION fn_pedido_existe (
        p_pedido_id IN pedido.pedido_id%TYPE
    ) RETURN NUMBER;

    FUNCTION fn_total_pedido (
        p_pedido_id IN pedido.pedido_id%TYPE
    ) RETURN NUMBER;

END pkg_pedido_app;
/

CREATE OR REPLACE PACKAGE BODY pkg_pedido_app AS

    PROCEDURE sp_crear_pedido (
        p_cliente_id  IN pedido.cliente_id%TYPE,
        p_estado      IN pedido.estado%TYPE,
        p_pedido_id_o OUT pedido.pedido_id%TYPE
    ) AS
        v_existe_cliente NUMBER;
    BEGIN
        v_existe_cliente := pkg_cliente_app.fn_cliente_existe(p_cliente_id);
        IF v_existe_cliente = 0 THEN
            raise_application_error(-23001, 'EL CLIENTE NO EXISTE');
        END IF;
        IF p_estado IS NULL THEN
            raise_application_error(-23002, 'EL ESTADO DEL PEDIDO ES OBLIGATORIO');
        END IF;
        INSERT INTO pedido (
            cliente_id,
            fecha,
            estado
        ) VALUES ( p_cliente_id,
                   sysdate,
                   p_estado ) RETURNING pedido_id INTO p_pedido_id_o;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-23999, 'ERROR AL CREAR PEDIDO: ' || sqlerrm);
    END sp_crear_pedido;

    PROCEDURE sp_actualizar_estado_pedido (
        p_pedido_id    IN pedido.pedido_id%TYPE,
        p_nuevo_estado IN pedido.estado%TYPE
    ) AS
        v_existe NUMBER;
    BEGIN
        v_existe := fn_pedido_existe(p_pedido_id);
        IF v_existe = 0 THEN
            raise_application_error(-23010, 'EL PEDIDO A ACTUALIZAR NO EXISTE');
        END IF;
        IF p_nuevo_estado IS NULL THEN
            raise_application_error(-23011, 'EL NUEVO ESTADO ES OBLIGATORIO');
        END IF;
        UPDATE pedido
        SET
            estado = p_nuevo_estado
        WHERE
            pedido_id = p_pedido_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-23012, 'NO SE ACTUALIZO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-23998, 'ERROR AL ACTUALIZAR ESTADO DEL PEDIDO: ' || sqlerrm);
    END sp_actualizar_estado_pedido;

    PROCEDURE sp_eliminar_pedido (
        p_pedido_id IN pedido.pedido_id%TYPE
    ) AS
        v_existe NUMBER;
    BEGIN
        v_existe := fn_pedido_existe(p_pedido_id);
        IF v_existe = 0 THEN
            raise_application_error(-23020, 'EL PEDIDO A ELIMINAR NO EXISTE');
        END IF;
        DELETE FROM pedido
        WHERE
            pedido_id = p_pedido_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-23021, 'NO SE ELIMINO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-23997, 'ERROR AL ELIMINAR PEDIDO: ' || sqlerrm);
    END sp_eliminar_pedido;

    PROCEDURE sp_obtener_pedido (
        p_pedido_id    IN pedido.pedido_id%TYPE,
        p_cliente_id_o OUT pedido.cliente_id%TYPE,
        p_fecha_o      OUT pedido.fecha%TYPE,
        p_estado_o     OUT pedido.estado%TYPE
    ) AS
    BEGIN
        SELECT
            cliente_id,
            fecha,
            estado
        INTO
            p_cliente_id_o,
            p_fecha_o,
            p_estado_o
        FROM
            pedido
        WHERE
            pedido_id = p_pedido_id;

    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error(-23030, 'NO SE ENCONTRO EL PEDIDO INDICADO');
        WHEN too_many_rows THEN
            raise_application_error(-23031, 'HAY MAS DE UN PEDIDO CON ESE ID');
        WHEN OTHERS THEN
            raise_application_error(-23996, 'ERROR AL OBTENER PEDIDO: ' || sqlerrm);
    END sp_obtener_pedido;

    PROCEDURE sp_listar_pedidos (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                p.pedido_id,
                                                p.cliente_id,
                                                c.nombre                                    AS nombre_cliente,
                                                p.fecha,
                                                p.estado,
                                                pkg_pedido_app.fn_total_pedido(p.pedido_id) AS total_pedido
                                            FROM
                                                     pedido p
                                                JOIN cliente c ON c.cliente_id = p.cliente_id
                          ORDER BY
                              p.fecha DESC;

    END sp_listar_pedidos;

    PROCEDURE sp_agregar_detalle_pedido (
        p_pedido_id    IN detallepedido.pedido_id%TYPE,
        p_producto_id  IN detallepedido.producto_id%TYPE,
        p_cantidad     IN detallepedido.cantidad%TYPE,
        p_detalle_id_o OUT detallepedido.detalle_pedido_id%TYPE
    ) AS
        v_existe_pedido   NUMBER;
        v_existe_producto NUMBER;
        v_precio          producto.precio%TYPE;
        v_subtotal        detallepedido.subtotal%TYPE;
    BEGIN
        v_existe_pedido := fn_pedido_existe(p_pedido_id);
        IF v_existe_pedido = 0 THEN
            raise_application_error(-23040, 'EL PEDIDO NO EXISTE');
        END IF;
        v_existe_producto := pkg_producto_app.fn_producto_existe(p_producto_id);
        IF v_existe_producto = 0 THEN
            raise_application_error(-23041, 'EL PRODUCTO NO EXISTE');
        END IF;
        IF p_cantidad IS NULL
           OR p_cantidad <= 0 THEN
            raise_application_error(-23042, 'LA CANTIDAD DEBE SER MAYOR A CERO');
        END IF;

        SELECT
            precio
        INTO v_precio
        FROM
            producto
        WHERE
            producto_id = p_producto_id;

        v_subtotal := v_precio * p_cantidad;
        INSERT INTO detallepedido (
            pedido_id,
            producto_id,
            cantidad,
            subtotal
        ) VALUES ( p_pedido_id,
                   p_producto_id,
                   p_cantidad,
                   v_subtotal ) RETURNING detalle_pedido_id INTO p_detalle_id_o;

        COMMIT;
    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error(-23043, 'NO SE ENCONTRO EL PRODUCTO PARA OBTENER PRECIO');
        WHEN OTHERS THEN
            raise_application_error(-23995, 'ERROR AL AGREGAR DETALLE DEL PEDIDO: ' || sqlerrm);
    END sp_agregar_detalle_pedido;

    PROCEDURE sp_listar_detalle_pedido (
        p_pedido_id IN detallepedido.pedido_id%TYPE,
        p_cursor    OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                                                d.detalle_pedido_id,
                                                d.pedido_id,
                                                d.producto_id,
                                                p.nombre AS nombre_producto,
                                                d.cantidad,
                                                d.subtotal
                                            FROM
                                                     detallepedido d
                                                JOIN producto p ON p.producto_id = d.producto_id
                          WHERE
                              d.pedido_id = p_pedido_id
                          ORDER BY
                              d.detalle_pedido_id;

    END sp_listar_detalle_pedido;

    PROCEDURE sp_actualizar_detalle_pedido (
        p_detalle_id  IN detallepedido.detalle_pedido_id%TYPE,
        p_producto_id IN detallepedido.producto_id%TYPE,
        p_cantidad    IN detallepedido.cantidad%TYPE
    ) AS
        v_existe_detalle  NUMBER;
        v_existe_producto NUMBER;
        v_precio          producto.precio%TYPE;
        v_subtotal        detallepedido.subtotal%TYPE;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_existe_detalle
        FROM
            detallepedido
        WHERE
            detalle_pedido_id = p_detalle_id;

        IF v_existe_detalle = 0 THEN
            raise_application_error(-23050, 'EL DETALLE DE PEDIDO NO EXISTE');
        END IF;
        v_existe_producto := pkg_producto_app.fn_producto_existe(p_producto_id);
        IF v_existe_producto = 0 THEN
            raise_application_error(-23051, 'EL PRODUCTO INDICADO NO EXISTE');
        END IF;
        IF p_cantidad IS NULL
           OR p_cantidad <= 0 THEN
            raise_application_error(-23052, 'LA CANTIDAD DEBE SER MAYOR A CERO');
        END IF;

        SELECT
            precio
        INTO v_precio
        FROM
            producto
        WHERE
            producto_id = p_producto_id;

        v_subtotal := v_precio * p_cantidad;
        UPDATE detallepedido
        SET
            producto_id = p_producto_id,
            cantidad = p_cantidad,
            subtotal = v_subtotal
        WHERE
            detalle_pedido_id = p_detalle_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-23053, 'NO SE ACTUALIZO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error(-23054, 'NO SE ENCONTRO EL PRODUCTO PARA OBTENER PRECIO');
        WHEN OTHERS THEN
            raise_application_error(-23994, 'ERROR AL ACTUALIZAR DETALLE DEL PEDIDO: ' || sqlerrm);
    END sp_actualizar_detalle_pedido;

    PROCEDURE sp_eliminar_detalle_pedido (
        p_detalle_id IN detallepedido.detalle_pedido_id%TYPE
    ) AS
    BEGIN
        DELETE FROM detallepedido
        WHERE
            detalle_pedido_id = p_detalle_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-20055, 'NO SE ELIMINÓ NINGÚN DETALLE (ID no existe)');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            IF sqlcode = -2292 THEN
                raise_application_error(-20096, 'NO SE PUEDE ELIMINAR EL DETALLE: EXISTEN DEVOLUCIONES ASOCIADAS.');
            ELSE
                raise_application_error(-20093, 'ERROR AL ELIMINAR DETALLE DEL PEDIDO: ' || sqlerrm);
            END IF;
    END sp_eliminar_detalle_pedido;

    FUNCTION fn_pedido_existe (
        p_pedido_id IN pedido.pedido_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            pedido
        WHERE
            pedido_id = p_pedido_id;

        IF v_cant > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END fn_pedido_existe;

    FUNCTION fn_total_pedido (
        p_pedido_id IN pedido.pedido_id%TYPE
    ) RETURN NUMBER AS
        v_total NUMBER;
    BEGIN
        SELECT
            nvl(
                sum(subtotal),
                0
            )
        INTO v_total
        FROM
            detallepedido
        WHERE
            pedido_id = p_pedido_id;

        RETURN v_total;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END fn_total_pedido;

END pkg_pedido_app;
/