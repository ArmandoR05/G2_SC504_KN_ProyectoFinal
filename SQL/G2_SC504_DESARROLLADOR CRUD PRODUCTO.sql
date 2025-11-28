CREATE OR REPLACE PACKAGE pkg_producto AS
  PROCEDURE listar(p_cursor OUT SYS_REFCURSOR);
  PROCEDURE crear(p_nombre IN NVARCHAR2, p_categoria IN NVARCHAR2,
                  p_descripcion IN NVARCHAR2, p_precio IN NUMBER,
                  p_producto_id OUT NUMBER);
  PROCEDURE actualizar(p_producto_id IN NUMBER, p_nombre IN NVARCHAR2,
                       p_categoria IN NVARCHAR2, p_descripcion IN NVARCHAR2,
                       p_precio IN NUMBER);
  PROCEDURE obtener_por_id(p_producto_id IN NUMBER, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE eliminar(p_producto_id IN NUMBER);

  PROCEDURE buscar_por_categoria(p_categoria IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE buscar_por_nombre(p_texto IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR);
END pkg_producto;
/

CREATE OR REPLACE PACKAGE BODY pkg_producto AS
  PROCEDURE listar(p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT producto_id, nombre, categoria, descripcion, precio
      FROM producto
      ORDER BY producto_id DESC;
  END;

  PROCEDURE crear(p_nombre IN NVARCHAR2, p_categoria IN NVARCHAR2,
                  p_descripcion IN NVARCHAR2, p_precio IN NUMBER,
                  p_producto_id OUT NUMBER) IS
  BEGIN
    INSERT INTO producto(nombre, categoria, descripcion, precio)
    VALUES (p_nombre, p_categoria, p_descripcion, p_precio)
    RETURNING producto_id INTO p_producto_id;
  END;

  PROCEDURE actualizar(p_producto_id IN NUMBER, p_nombre IN NVARCHAR2,
                       p_categoria IN NVARCHAR2, p_descripcion IN NVARCHAR2,
                       p_precio IN NUMBER) IS
  BEGIN
    UPDATE producto
       SET nombre      = p_nombre,
           categoria   = p_categoria,
           descripcion = p_descripcion,
           precio      = p_precio
     WHERE producto_id = p_producto_id;
  END;

  PROCEDURE obtener_por_id(p_producto_id IN NUMBER, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT producto_id, nombre, categoria, descripcion, precio
      FROM producto
      WHERE producto_id = p_producto_id;
  END;

  PROCEDURE eliminar(p_producto_id IN NUMBER) IS
  BEGIN
    DELETE FROM producto WHERE producto_id = p_producto_id;
  END;

  PROCEDURE buscar_por_categoria(p_categoria IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT producto_id, nombre, categoria, descripcion, precio
      FROM producto
      WHERE UPPER(categoria) = UPPER(p_categoria)
      ORDER BY nombre;
  END;

  PROCEDURE buscar_por_nombre(p_texto IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT producto_id, nombre, categoria, descripcion, precio
      FROM producto
      WHERE UPPER(nombre) LIKE '%'||UPPER(p_texto)||'%'
      ORDER BY nombre;
  END;
END pkg_producto;
/




--TRIGGERS
--CREAR INVENTARIO CUANDO SE HAGA PRODUCTO
CREATE OR REPLACE TRIGGER trg_producto_ai_ins_inv
AFTER INSERT ON producto
FOR EACH ROW
BEGIN
  INSERT INTO inventario(producto_id, cantidad_actual)
  VALUES (:NEW.producto_id, 0);
END;
/

//Cuando se elimina un producto, eliminar inventario
CREATE OR REPLACE TRIGGER TRG_PRODUCTO_AD_DEL_INV
AFTER DELETE ON PRODUCTO
FOR EACH ROW
BEGIN
    DELETE FROM INVENTARIO
    WHERE PRODUCTO_ID = :OLD.PRODUCTO_ID;
END;
/

CREATE OR REPLACE TRIGGER trg_producto_bu_precio
BEFORE UPDATE OF precio ON producto
FOR EACH ROW
BEGIN
  IF :NEW.precio < 0 THEN
    RAISE_APPLICATION_ERROR(-20010, 'El precio no puede ser negativo');
  END IF;
END;
/


--FUNCIONES



-- existencia
CREATE OR REPLACE FUNCTION fn_producto_existe(p_producto_id IN NUMBER)
RETURN NUMBER IS v NUMBER;
BEGIN
  SELECT COUNT(*) INTO v FROM producto WHERE producto_id = p_producto_id;
  RETURN v;
END;
/
-- Nombre del producto
CREATE OR REPLACE FUNCTION fn_nombre_producto(p_producto_id IN NUMBER)
RETURN NVARCHAR2 IS v NVARCHAR2(200);
BEGIN
  SELECT nombre INTO v FROM producto WHERE producto_id = p_producto_id;
  RETURN v;
END;
/
-- Precio del producto
CREATE OR REPLACE FUNCTION fn_producto_precio(p_producto_id IN NUMBER)
RETURN NUMBER IS v NUMBER;
BEGIN
  SELECT precio INTO v FROM producto WHERE producto_id = p_producto_id;
  RETURN v;
END;
/
-- Valor de inventario de ese producto 
CREATE OR REPLACE FUNCTION fn_valor_inventario_producto(p_producto_id IN NUMBER)
RETURN NUMBER IS v NUMBER;
BEGIN
  SELECT NVL(i.cantidad_actual,0) * p.precio
  INTO v
  FROM producto p LEFT JOIN inventario i ON i.producto_id = p.producto_id
  WHERE p.producto_id = p_producto_id;
  RETURN NVL(v,0);
END;
/



--VISTAS
--  Productos básicos
CREATE OR REPLACE VIEW vw_productos AS
SELECT producto_id, nombre, categoria, descripcion, precio
FROM producto;

--  Productos por categoría
CREATE OR REPLACE VIEW vw_productos_por_categoria AS
SELECT categoria, producto_id, nombre, precio
FROM producto;


--TRIGGER

CREATE OR REPLACE TRIGGER trg_producto_bu_precio
BEFORE UPDATE OF precio ON producto
FOR EACH ROW
BEGIN
  IF :NEW.precio < 0 THEN
    RAISE_APPLICATION_ERROR(-20010, 'El precio no puede ser negativo');
  END IF;
END;
/









