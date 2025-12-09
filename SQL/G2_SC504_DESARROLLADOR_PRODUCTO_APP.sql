SHOW CON_NAME;
SHOW USER;

--DECLARACIÓN 

CREATE OR REPLACE PACKAGE pkg_producto_app AS
    PROCEDURE sp_crear_producto (
        p_nombre        IN producto.nombre%TYPE,
        p_categoria     IN producto.categoria%TYPE,
        p_descripcion   IN producto.descripcion%TYPE,
        p_precio        IN producto.precio%TYPE,
        p_producto_id_o OUT producto.producto_id%TYPE
    );

    PROCEDURE sp_actualizar_producto (
        p_producto_id IN producto.producto_id%TYPE,
        p_nombre      IN producto.nombre%TYPE,
        p_categoria   IN producto.categoria%TYPE,
        p_descripcion IN producto.descripcion%TYPE,
        p_precio      IN producto.precio%TYPE
    );

    PROCEDURE sp_eliminar_producto (
        p_producto_id IN producto.producto_id%TYPE
    );

    PROCEDURE sp_obtener_producto (
        p_producto_id IN producto.producto_id%TYPE,
        p_nombre      OUT producto.nombre%TYPE,
        p_categoria   OUT producto.categoria%TYPE,
        p_descripcion OUT producto.descripcion%TYPE,
        p_precio      OUT producto.precio%TYPE
    );

    PROCEDURE sp_listar_productos (
        p_cursor OUT SYS_REFCURSOR
    );

    FUNCTION fn_producto_existe (
        p_producto_id IN producto.producto_id%TYPE
    ) RETURN NUMBER;

    FUNCTION fn_precio_producto (
        p_producto_id IN producto.producto_id%TYPE
    ) RETURN producto.precio%TYPE;
    
    FUNCTION FN_STOCK_PRODUCTO (
    p_producto_id IN PRODUCTO.PRODUCTO_ID%TYPE
    ) RETURN NUMBER;


END pkg_producto_app;
/


--CUERPO

CREATE OR REPLACE PACKAGE BODY pkg_producto_app AS
--1)
    PROCEDURE sp_crear_producto (
        p_nombre        IN producto.nombre%TYPE,
        p_categoria     IN producto.categoria%TYPE,
        p_descripcion   IN producto.descripcion%TYPE,
        p_precio        IN producto.precio%TYPE,
        p_producto_id_o OUT producto.producto_id%TYPE
    ) AS
        v_cant NUMBER;
    BEGIN
        IF p_nombre IS NULL THEN
            raise_application_error(-21001, 'EL NOMBRE DEL PRODUCTO ES OBLIGATORIO');
        END IF;
        IF p_precio IS NULL
           OR p_precio <= 0 THEN
            raise_application_error(-21002, 'EL PRECIO DEBE SER MAYOR A CERO');
        END IF;

        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            producto
        WHERE
            upper(nombre) = upper(p_nombre);

        IF v_cant > 0 THEN
            raise_application_error(-21003, 'YA EXISTE UN PRODUCTO CON ESE NOMBRE');
        END IF;
        INSERT INTO producto (
            nombre,
            categoria,
            descripcion,
            precio
        ) VALUES ( p_nombre,
                   p_categoria,
                   p_descripcion,
                   p_precio ) RETURNING producto_id INTO p_producto_id_o;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-21999, 'ERROR AL CREAR PRODUCTO: ' || sqlerrm);
    END sp_crear_producto;
--2)
    PROCEDURE sp_actualizar_producto (
        p_producto_id IN producto.producto_id%TYPE,
        p_nombre      IN producto.nombre%TYPE,
        p_categoria   IN producto.categoria%TYPE,
        p_descripcion IN producto.descripcion%TYPE,
        p_precio      IN producto.precio%TYPE
    ) AS
        v_existe NUMBER;
    BEGIN
        v_existe := fn_producto_existe(p_producto_id);
        IF v_existe = 0 THEN
            raise_application_error(-21010, 'EL PRODUCTO A ACTUALIZAR NO EXISTE');
        END IF;
        IF p_precio IS NULL
           OR p_precio <= 0 THEN
            raise_application_error(-21011, 'EL PRECIO DEBE SER MAYOR A CERO');
        END IF;

        UPDATE producto
        SET
            nombre = p_nombre,
            categoria = p_categoria,
            descripcion = p_descripcion,
            precio = p_precio
        WHERE
            producto_id = p_producto_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-21012, 'NO SE ACTUALIZO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-21998, 'ERROR AL ACTUALIZAR PRODUCTO: ' || sqlerrm);
    END sp_actualizar_producto;


--3)
    PROCEDURE sp_eliminar_producto (
        p_producto_id IN producto.producto_id%TYPE
    ) AS
        v_existe NUMBER;
    BEGIN
        v_existe := fn_producto_existe(p_producto_id);
        IF v_existe = 0 THEN
            raise_application_error(-21020, 'EL PRODUCTO A ELIMINAR NO EXISTE');
        END IF;
        DELETE FROM producto
        WHERE
            producto_id = p_producto_id;

        IF SQL%rowcount = 0 THEN
            raise_application_error(-21021, 'NO SE ELIMINO NINGUN REGISTRO');
        END IF;
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            raise_application_error(-21997, 'ERROR AL ELIMINAR PRODUCTO: ' || sqlerrm);
    END sp_eliminar_producto;

--4)
    PROCEDURE sp_obtener_producto (
        p_producto_id IN producto.producto_id%TYPE,
        p_nombre      OUT producto.nombre%TYPE,
        p_categoria   OUT producto.categoria%TYPE,
        p_descripcion OUT producto.descripcion%TYPE,
        p_precio      OUT producto.precio%TYPE
    ) AS
    BEGIN
        SELECT
            nombre,
            categoria,
            descripcion,
            precio
        INTO
            p_nombre,
            p_categoria,
            p_descripcion,
            p_precio
        FROM
            producto
        WHERE
            producto_id = p_producto_id;

    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error(-21030, 'NO SE ENCONTRO EL PRODUCTO');
        WHEN too_many_rows THEN
            raise_application_error(-21031, 'HAY MAS DE UN PRODUCTO CON ESE ID');
        WHEN OTHERS THEN
            raise_application_error(-21996, 'ERROR AL OBTENER PRODUCTO: ' || sqlerrm);
    END sp_obtener_producto;

--5
    PROCEDURE sp_listar_productos (
        p_cursor OUT SYS_REFCURSOR       --PARA GUI
    ) AS
    BEGIN
        OPEN p_cursor FOR SELECT
        producto_id,
        nombre,
        categoria,
        descripcion,
        precio
   FROM
       producto
   ORDER BY
       nombre;

    END sp_listar_productos;

    FUNCTION fn_producto_existe (
        p_producto_id IN producto.producto_id%TYPE
    ) RETURN NUMBER AS
        v_cant NUMBER;
    BEGIN
        SELECT
            COUNT(*)
        INTO v_cant
        FROM
            producto
        WHERE
            producto_id = p_producto_id;

        IF v_cant > 0 THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 0;
    END fn_producto_existe;

    FUNCTION fn_precio_producto (
        p_producto_id IN producto.producto_id%TYPE
    ) RETURN producto.precio%TYPE AS
        v_precio producto.precio%TYPE;
    BEGIN
        SELECT
            precio
        INTO v_precio
        FROM
            producto
        WHERE
            producto_id = p_producto_id;

        RETURN v_precio;
    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error(-21040, 'NO EXISTE EL PRODUCTO PARA OBTENER PRECIO');
        WHEN OTHERS THEN
            raise_application_error(-21995, 'ERROR AL OBTENER PRECIO: ' || sqlerrm);
    END fn_precio_producto;
    
    FUNCTION fn_stock_producto (
        p_producto_id IN producto.producto_id%TYPE
    ) RETURN NUMBER AS
        v_stock NUMBER;
    BEGIN
        SELECT
            cantidad_actual
        INTO v_stock
        FROM
            inventario
        WHERE
            producto_id = p_producto_id;

    RETURN NVL(v_stock,0);
    END FN_STOCK_PRODUCTO;

END pkg_producto_app;
/


