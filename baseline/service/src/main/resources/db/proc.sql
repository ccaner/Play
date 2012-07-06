CREATE PROCEDURE load_pets_grouped(
    IN name   VARCHAR(64),
    IN age    INTEGER)
PARAMETER STYLE JAVA
LANGUAGE JAVA
READS SQL DATA
DYNAMIC RESULT SETS 2
EXTERNAL NAME 'play.baseline.db.StoredProcedures.load_pets_grouped';

CREATE PROCEDURE count_by_age(
    IN age    INTEGER,
    OUT count INTEGER)
PARAMETER STYLE JAVA
LANGUAGE JAVA
READS SQL DATA
EXTERNAL NAME 'play.baseline.db.StoredProcedures.count_by_age';
