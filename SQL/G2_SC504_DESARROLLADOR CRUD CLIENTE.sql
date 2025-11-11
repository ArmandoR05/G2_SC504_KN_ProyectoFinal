CREATE OR REPLACE PACKAGE pkg_cliente AS
  PROCEDURE listar(p_cursor OUT SYS_REFCURSOR);
  PROCEDURE crear(p_nombre IN NVARCHAR2, p_correo IN NVARCHAR2,
                  p_telefono IN NVARCHAR2, p_direccion IN NVARCHAR2,
                  p_cliente_id OUT NUMBER);
  PROCEDURE actualizar(p_cliente_id IN NUMBER, p_nombre IN NVARCHAR2,
                       p_correo IN NVARCHAR2, p_telefono IN NVARCHAR2,
                       p_direccion IN NVARCHAR2);
  PROCEDURE obtener_por_id(p_cliente_id IN NUMBER, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE eliminar(p_cliente_id IN NUMBER);

  PROCEDURE buscar_por_nombre(p_texto IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR);
  PROCEDURE buscar_por_correo(p_correo IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR);
END pkg_cliente;
/

CREATE OR REPLACE PACKAGE BODY pkg_cliente AS
  PROCEDURE listar(p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT cliente_id, nombre, correo, telefono, direccion
      FROM cliente
      ORDER BY cliente_id;
  END;

  PROCEDURE crear(p_nombre IN NVARCHAR2, p_correo IN NVARCHAR2,
                  p_telefono IN NVARCHAR2, p_direccion IN NVARCHAR2,
                  p_cliente_id OUT NUMBER) IS
  BEGIN
    INSERT INTO cliente(nombre, correo, telefono, direccion)
    VALUES(p_nombre, p_correo, p_telefono, p_direccion)
    RETURNING cliente_id INTO p_cliente_id;
  END;

  PROCEDURE actualizar(p_cliente_id IN NUMBER, p_nombre IN NVARCHAR2,
                       p_correo IN NVARCHAR2, p_telefono IN NVARCHAR2,
                       p_direccion IN NVARCHAR2) IS
  BEGIN
    UPDATE cliente
       SET nombre   = p_nombre,
           correo   = p_correo,
           telefono = p_telefono,
           direccion= p_direccion
     WHERE cliente_id = p_cliente_id;
  END;

  PROCEDURE obtener_por_id(p_cliente_id IN NUMBER, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT cliente_id, nombre, correo, telefono, direccion
      FROM cliente
      WHERE cliente_id = p_cliente_id;
  END;

  PROCEDURE eliminar(p_cliente_id IN NUMBER) IS
  BEGIN
    DELETE FROM cliente WHERE cliente_id = p_cliente_id;
  END;

  PROCEDURE buscar_por_nombre(p_texto IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT cliente_id, nombre, correo, telefono, direccion
      FROM cliente
      WHERE UPPER(nombre) LIKE '%'||UPPER(p_texto)||'%'
      ORDER BY nombre;
  END;

  PROCEDURE buscar_por_correo(p_correo IN NVARCHAR2, p_cursor OUT SYS_REFCURSOR) IS
  BEGIN
    OPEN p_cursor FOR
      SELECT cliente_id, nombre, correo, telefono, direccion
      FROM cliente
      WHERE LOWER(correo) = LOWER(p_correo);
  END;
END pkg_cliente;
/





--triggers
-- Normaliza correo a minúsculas y actualiza fecha de modificación
CREATE OR REPLACE TRIGGER trg_cliente_bi_norm
BEFORE INSERT OR UPDATE ON cliente
FOR EACH ROW
BEGIN
  IF :NEW.correo IS NOT NULL THEN
    :NEW.correo := LOWER(:NEW.correo);
  END IF;
END;
/


--FUNCIONES

CREATE OR REPLACE FUNCTION fn_cliente_existe(p_cliente_id IN NUMBER)
RETURN NUMBER IS v NUMBER;
BEGIN
  SELECT COUNT(*) INTO v FROM cliente WHERE cliente_id = p_cliente_id;
  RETURN v;
END;
/

-- Nombre del cliente
CREATE OR REPLACE FUNCTION fn_nombre_cliente(p_cliente_id IN NUMBER)
RETURN NVARCHAR2 IS v NVARCHAR2(200);
BEGIN
  SELECT nombre INTO v FROM cliente WHERE cliente_id = p_cliente_id;
  RETURN v;
END;
/

-- correo registrado
CREATE OR REPLACE FUNCTION fn_cliente_tiene_correo(p_cliente_id IN NUMBER)
RETURN CHAR IS v NUMBER;
BEGIN
  SELECT COUNT(*) INTO v FROM cliente WHERE cliente_id = p_cliente_id AND correo IS NOT NULL;
  RETURN CASE WHEN v > 0 THEN 'S' ELSE 'N' END;
END;
/



