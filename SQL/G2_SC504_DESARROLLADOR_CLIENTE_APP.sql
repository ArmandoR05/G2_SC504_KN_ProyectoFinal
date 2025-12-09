CREATE OR REPLACE PACKAGE pkg_cliente_app AS
    PROCEDURE sp_crear_cliente (
        p_nombre       IN cliente.nombre%TYPE,
        p_correo       IN cliente.correo%TYPE,
        p_telefono     IN cliente.telefono%TYPE,
        p_direccion    IN cliente.direccion%TYPE,
        p_cliente_id_o OUT cliente.cliente_id%TYPE
    );

    PROCEDURE sp_actualizar_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE,
        p_nombre     IN cliente.nombre%TYPE,
        p_correo     IN cliente.correo%TYPE,
        p_telefono   IN cliente.telefono%TYPE,
        p_direccion  IN cliente.direccion%TYPE
    );

    PROCEDURE sp_eliminar_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE
    );

    PROCEDURE sp_obtener_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE,
        p_nombre     OUT cliente.nombre%TYPE,
        p_correo     OUT cliente.correo%TYPE,
        p_telefono   OUT cliente.telefono%TYPE,
        p_direccion  OUT cliente.direccion%TYPE
    );

    PROCEDURE sp_listar_clientes (
        p_cursor OUT SYS_REFCURSOR
    );

    FUNCTION fn_cliente_existe (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) RETURN NUMBER;

    FUNCTION fn_cliente_correo_existe (
        p_correo IN cliente.correo%TYPE
    ) RETURN NUMBER;
    
    FUNCTION FN_TOTAL_PEDIDOS_CLIENTE (
        p_cliente_id IN CLIENTE.CLIENTE_ID%TYPE
    ) RETURN NUMBER;
    
    FUNCTION FN_VALIDAR_CORREO_CLIENTE (
        p_correo IN CLIENTE.CORREO%TYPE
    ) RETURN NUMBER;



END pkg_cliente_app;
/

CREATE OR REPLACE PACKAGE BODY pkg_cliente_app AS

    PROCEDURE sp_crear_cliente (
        p_nombre       IN cliente.nombre%TYPE,
        p_correo       IN cliente.correo%TYPE,
        p_telefono     IN cliente.telefono%TYPE,
        p_direccion    IN cliente.direccion%TYPE,
        p_cliente_id_o OUT cliente.cliente_id%TYPE
    ) AS
        v_cant NUMBER;
    BEGIN
        IF p_nombre IS NULL THEN
            raise_application_error(-22001, 'EL NOMBRE DEL CLIENTE ES OBLIGATORIO');
        END IF;
        IF p_correo IS NULL THEN
            raise_application_error(-22002, 'EL CORREO DEL CLIENTE ES OBLIGATORIO');
        END IF;
        IF NOT regexp_like(p_correo, '^[^@]+@[^@]+\.[^@]+$') THEN
            raise_application_error(-22003, 'EL CORREO DEL CLIENTE NO TIENE FORMATO VALIDO');
        END IF;

        IF p_telefono IS NULL THEN
            raise_application_error(-22004, 'EL TELEFONO DEL CLIENTE ES OBLIGATORIO');
        END IF;
        IF p_direccion IS NULL THEN
            raise_application_error(-22005, 'LA DIRECCION DEL CLIENTE ES OBLIGATORIA');
        END IF;
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            cliente
        WHERE
            upper(correo) = upper(p_correo);

        IF v_cant > 0 THEN
            raise_application_error(-22006, 'YA EXISTE UN CLIENTE CON ESE CORREO');
        END IF;
        INSERT INTO cliente (
            nombre,
            correo,
            telefono,
            direccion
        ) VALUES ( p_nombre,
                   p_correo,
                   p_telefono,
                   p_direccion ) RETURNING cliente_id INTO p_cliente_id_o;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-22999, 'ERROR AL CREAR CLIENTE: ' || sqlerrm);
    END sp_crear_cliente;

    PROCEDURE sp_actualizar_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE,
        p_nombre     IN cliente.nombre%TYPE,
        p_correo     IN cliente.correo%TYPE,
        p_telefono   IN cliente.telefono%TYPE,
        p_direccion  IN cliente.direccion%TYPE
    ) AS
        v_existe NUMBER;
        v_cant   NUMBER;
    BEGIN
        v_existe := fn_cliente_existe(p_cliente_id);
        IF v_existe = 0 THEN
            raise_application_error(-22010, 'EL CLIENTE A ACTUALIZAR NO EXISTE');
        END IF;
        IF p_nombre IS NULL THEN
            raise_application_error(-22011, 'EL NOMBRE DEL CLIENTE ES OBLIGATORIO');
        END IF;
        IF p_correo IS NULL THEN
            raise_application_error(-22012, 'EL CORREO DEL CLIENTE ES OBLIGATORIO');
        END IF;
        IF NOT regexp_like(p_correo, '^[^@]+@[^@]+\.[^@]+$') THEN
            raise_application_error(-22013, 'EL CORREO DEL CLIENTE NO TIENE FORMATO VALIDO');
        END IF;

        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            cliente
        WHERE
                upper(correo) = upper(p_correo)
            AND cliente_id <> p_cliente_id;

        IF v_cant > 0 THEN
            raise_application_error(-22014, 'YA EXISTE OTRO CLIENTE CON ESE CORREO');
        END IF;
        IF p_telefono IS NULL THEN
            raise_application_error(-22015, 'EL TELEFONO DEL CLIENTE ES OBLIGATORIO');
        END IF;
        IF p_direccion IS NULL THEN
            raise_application_error(-22016, 'LA DIRECCION DEL CLIENTE ES OBLIGATORIA');
        END IF;
        UPDATE cliente
        SET
            nombre = p_nombre,
            correo = p_correo,
            telefono = p_telefono,
            direccion = p_direccion
        WHERE
            cliente_id = p_cliente_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-22017, 'NO SE ACTUALIZO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-22998, 'ERROR AL ACTUALIZAR CLIENTE: ' || sqlerrm);
    END sp_actualizar_cliente;

    PROCEDURE sp_eliminar_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) AS
        v_existe NUMBER;
    BEGIN
        v_existe := fn_cliente_existe(p_cliente_id);
        IF v_existe = 0 THEN
            raise_application_error(-22020, 'EL CLIENTE A ELIMINAR NO EXISTE');
        END IF;
        DELETE FROM cliente
        WHERE
            cliente_id = p_cliente_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-22021, 'NO SE ELIMINO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-22997, 'ERROR AL ELIMINAR CLIENTE: ' || sqlerrm);
    END sp_eliminar_cliente;

    PROCEDURE sp_obtener_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE,
        p_nombre     OUT cliente.nombre%TYPE,
        p_correo     OUT cliente.correo%TYPE,
        p_telefono   OUT cliente.telefono%TYPE,
        p_direccion  OUT cliente.direccion%TYPE
    ) AS
    BEGIN
        SELECT
            nombre,
            correo,
            telefono,
            direccion
        INTO
            p_nombre,
            p_correo,
            p_telefono,
            p_direccion
        FROM
            cliente
        WHERE
            cliente_id = p_cliente_id;

    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error(-22030, 'NO SE ENCONTRO EL CLIENTE INDICADO');
        WHEN too_many_rows THEN
            raise_application_error(-22031, 'HAY MAS DE UN CLIENTE CON ESE ID');
        WHEN OTHERS THEN
            raise_application_error(-22996, 'ERROR AL OBTENER CLIENTE: ' || sqlerrm);
    END sp_obtener_cliente;

    PROCEDURE sp_listar_clientes (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
                         cliente_id,
                         nombre,
                         correo,
                         telefono,
                         direccion
                      FROM
                         cliente
                      ORDER BY
                         nombre;

    END sp_listar_clientes;

    FUNCTION fn_cliente_existe (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            cliente
        WHERE
            cliente_id = p_cliente_id;

        IF v_cant > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END fn_cliente_existe;

    FUNCTION fn_cliente_correo_existe (
        p_correo IN cliente.correo%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            cliente
        WHERE
            upper(correo) = upper(p_correo);

        RETURN v_cant;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END fn_cliente_correo_existe;
    
    FUNCTION fn_total_pedidos_cliente (
        p_cliente_id IN cliente.cliente_id%TYPE
    ) RETURN NUMBER AS
        v_total NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_total
        FROM
            pedido
        WHERE
            cliente_id = p_cliente_id;

        RETURN v_total;
    END fn_total_pedidos_cliente;
    
    FUNCTION fn_validar_correo_cliente (
        p_correo IN cliente.correo%TYPE
    ) RETURN NUMBER AS
    BEGIN
        IF regexp_like(p_correo, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$') THEN
            RETURN 1; 
        ELSE
            RETURN 0; 
        END IF;
    END fn_validar_correo_cliente;


END pkg_cliente_app;
/



