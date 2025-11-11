CREATE OR REPLACE PACKAGE pkg_inventario AS
  PROCEDURE listar(p_cursor OUT SYS_REFCURSOR);
  PROCEDURE listar_bajo_umbral(p_umbral IN NUMBER, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE buscar_por_nombre(p_texto IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE actualizar(p_producto_id IN NUMBER, p_cantidad IN NUMBER);
  PROCEDURE obtener_por_id(p_producto_id IN NUMBER, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE eliminar(p_producto_id IN NUMBER);
END pkg_inventario;
/

CREATE OR REPLACE PACKAGE BODY pkg_inventario AS
  PROCEDURE listar(p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT i.producto_id, p.nombre AS nombre_producto, i.cantidad_actual
      FROM inventario i JOIN producto p ON p.producto_id = i.producto_id
      ORDER BY i.producto_id;
  END;

  PROCEDURE listar_bajo_umbral(p_umbral IN NUMBER, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT i.producto_id, p.nombre AS nombre_producto, i.cantidad_actual
      FROM inventario i JOIN producto p ON p.producto_id = i.producto_id
      WHERE i.cantidad_actual < p_umbral
      ORDER BY i.cantidad_actual ASC;
  END;

  PROCEDURE buscar_por_nombre(p_texto IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT i.producto_id, p.nombre AS nombre_producto, i.cantidad_actual
      FROM inventario i JOIN producto p ON p.producto_id = i.producto_id
      WHERE UPPER(p.nombre) LIKE '%'||UPPER(p_texto)||'%'
      ORDER BY p.nombre;
  END;

  PROCEDURE actualizar(p_producto_id IN NUMBER, p_cantidad IN NUMBER) IS
  BEGIN
    UPDATE inventario
       SET cantidad_actual = p_cantidad
     WHERE producto_id = p_producto_id;
  END;

  PROCEDURE obtener_por_id(p_producto_id IN NUMBER, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT i.producto_id, p.nombre AS nombre_producto, i.cantidad_actual
      FROM inventario i JOIN producto p ON p.producto_id = i.producto_id
      WHERE i.producto_id = p_producto_id;
  END;

  PROCEDURE eliminar(p_producto_id IN NUMBER) IS
  BEGIN
    DELETE FROM inventario WHERE producto_id = p_producto_id;
  END;
END pkg_inventario;
/









CREATE OR REPLACE TRIGGER trg_inventario_bu_no_neg
BEFORE UPDATE OF cantidad_actual ON inventario
FOR EACH ROW
BEGIN
  IF :NEW.cantidad_actual < 0 THEN
    RAISE_APPLICATION_ERROR(-20001, 'La cantidad no puede ser negativa');
  END IF;
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

-- Evitar inventario negativo
CREATE OR REPLACE TRIGGER trg_inventario_bu_no_neg
BEFORE UPDATE OF cantidad_actual ON inventario
FOR EACH ROW
BEGIN
  IF :NEW.cantidad_actual < 0 THEN
    RAISE_APPLICATION_ERROR(-20001, 'La cantidad no puede ser negativa');
  END IF;
END;
/


--FUNCIONES
-- Stock disponible de un producto
CREATE OR REPLACE FUNCTION fn_stock_disponible(p_producto_id IN NUMBER)
RETURN NUMBER IS v NUMBER;
BEGIN
  SELECT NVL(cantidad_actual,0) INTO v FROM inventario WHERE producto_id = p_producto_id;
  RETURN v;
END;
/

-- stock
CREATE OR REPLACE FUNCTION fn_tiene_stock(p_producto_id IN NUMBER)
RETURN CHAR IS
BEGIN
  RETURN CASE WHEN fn_stock_disponible(p_producto_id) > 0 THEN 'S' ELSE 'N' END;
END;
/

--reponer
CREATE OR REPLACE FUNCTION fn_reponer_necesario(p_producto_id IN NUMBER, p_umbral IN NUMBER)
RETURN CHAR IS
BEGIN
  RETURN CASE WHEN fn_stock_disponible(p_producto_id) < p_umbral THEN 'S' ELSE 'N' END;
END;
/

-- Valor total de todo el inventario
CREATE OR REPLACE FUNCTION fn_valor_total_inventario
RETURN NUMBER IS v NUMBER;
BEGIN
  SELECT SUM(NVL(i.cantidad_actual,0)*p.precio)
  INTO v
  FROM producto p LEFT JOIN inventario i ON i.producto_id = p.producto_id;
  RETURN NVL(v,0);
END;
/

-- VISTAS
CREATE OR REPLACE VIEW vw_inventario AS
SELECT i.producto_id, p.nombre AS nombre_producto, i.cantidad_actual
FROM inventario i
JOIN producto p ON p.producto_id = i.producto_id;

CREATE OR REPLACE VIEW vw_inventario_bajo AS
SELECT *
FROM vw_inventario
WHERE cantidad_actual < 5;

CREATE OR REPLACE VIEW vw_inventario_sin_stock AS
SELECT *
FROM vw_inventario
WHERE cantidad_actual = 0;







