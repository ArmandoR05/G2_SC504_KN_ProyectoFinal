CREATE OR REPLACE PACKAGE pkg_inventario AS
    PROCEDURE listar (
        p_cursor OUT SYS_REFCURSOR
    );

    PROCEDURE actualizar (
        p_producto_id IN NUMBER,
        p_cantidad    IN NUMBER
    );

    PROCEDURE obtener_por_id (
        p_producto_id IN NUMBER,
        p_cursor      OUT SYS_REFCURSOR
    );

    PROCEDURE eliminar (
        p_producto_id IN NUMBER
    );

END pkg_inventario;
/




CREATE OR REPLACE PACKAGE BODY pkg_inventario AS

  PROCEDURE LISTAR(P_CURSOR OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN P_CURSOR FOR
      SELECT i.producto_id,
             p.nombre AS nombre_producto,
             i.cantidad_actual
        FROM inventario i
        JOIN producto p ON p.producto_id = i.producto_id
       ORDER BY i.producto_id;
  END LISTAR;

  PROCEDURE ACTUALIZAR(P_PRODUCTO_ID IN NUMBER, P_CANTIDAD IN NUMBER) IS
  BEGIN
    UPDATE inventario
       SET cantidad_actual = P_CANTIDAD
     WHERE producto_id = P_PRODUCTO_ID;
  END ACTUALIZAR;

  PROCEDURE OBTENER_POR_ID(P_PRODUCTO_ID IN NUMBER, P_CURSOR OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN P_CURSOR FOR
      SELECT i.producto_id,
             p.nombre AS nombre_producto,
             i.cantidad_actual
        FROM inventario i
        JOIN producto p ON p.producto_id = i.producto_id
       WHERE i.producto_id = P_PRODUCTO_ID;
  END OBTENER_POR_ID;

  PROCEDURE ELIMINAR(P_PRODUCTO_ID IN NUMBER) IS
  BEGIN
    DELETE FROM inventario WHERE producto_id = P_PRODUCTO_ID;
  END ELIMINAR;

END pkg_inventario;
/






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










