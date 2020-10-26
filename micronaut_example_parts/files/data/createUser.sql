DROP USER mnocidemo;

CREATE USER mnocidemo IDENTIFIED BY HandsOnLabUser1;

GRANT
CREATE SESSION,
RESOURCE,
UNLIMITED TABLESPACE,
CREATE TABLE,
CREATE VIEW,
CREATE SEQUENCE,
CREATE PROCEDURE,
CREATE TYPE,
CREATE SYNONYM
TO mnocidemo;

/* for SQL Developer Web */
BEGIN
 ords_admin.enable_schema(
  p_enabled => TRUE,
  p_schema => 'mnocidemo',
  p_url_mapping_type => 'BASE_PATH',
  p_url_mapping_pattern => 'mnocidemo',
  p_auto_rest_auth => NULL
 );
 COMMIT;
END;
/

EXIT;
