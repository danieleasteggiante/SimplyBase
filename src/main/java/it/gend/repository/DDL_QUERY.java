package it.gend.repository;

/**
 * @author Daniele Asteggiante
 */
public interface DDL_QUERY {
    String checkTableExistence = "SELECT 1 FROM ALL_TABLES WHERE TABLE_NAME = ?";
    String checkTriggerExistence = "SELECT 1 FROM ALL_TRIGGERS WHERE TRIGGER_NAME = ?";
    String DDL_LOG_CONF = "CREATE TABLE DDL_LOG_CONF(UTENTE VARCHAR2(16) not null, PASSWORD VARCHAR2(16) not NULL, DB_LOCKED NUMBER(1) NOT NULL)";
    String DDL_SCRIPT_VERSION = "CREATE TABLE DDL_SCRIPT_VERSION( SOFTWARE VARCHAR(16) NOT NULL, SOFTWARE_VERSION VARCHAR(10), SCRIPT_DATE DATE, CONTENT CLOB)";
    String DDL_LOG = "CREATE TABLE CARICAMENTO.DDL_LOG (EVENT_DATE TIMESTAMP (6), ORA_SYSEVENT VARCHAR2(100), ORA_DICT_OBJ_OWNER VARCHAR2(100), ORA_DICT_OBJ_NAME VARCHAR2(100), ORA_DICT_OBJ_TYPE VARCHAR2(100), ORA_LOGIN_USER VARCHAR2(100), DDL_TEXT CLOB)";
    String DDL_TRIGGER = "CREATE OR REPLACE TRIGGER CARICAMENTO.log_ddl " +
            "AFTER DDL ON DATABASE " +
            "DECLARE " +
            "sql_text ora_name_list_t; " +
            "  stmt CLOB; " +
            "  n NUMBER; " +
            "BEGIN " +
            "  n := ora_sql_txt(sql_text); " +
            "  stmt := ''; " +
            "FOR i IN 1..n LOOP " +
            "    stmt := stmt || sql_text(i); " +
            "END LOOP; " +
            "INSERT INTO ddl_log ( " +
            "    event_date, " +
            "    ora_sysevent, " +
            "    ora_dict_obj_owner, " +
            "    ora_dict_obj_name, " +
            "    ora_dict_obj_type, " +
            "    ora_login_user, " +
            "    ddl_text " +
            ") VALUES ( " +
            "             SYSTIMESTAMP, " +
            "             ora_sysevent, " +
            "             ora_dict_obj_owner, " +
            "             ora_dict_obj_name, " +
            "             ora_dict_obj_type, " +
            "             ora_login_user, " +
            "             stmt " +
            "         ); " +
            "END";

    String DDL_INSERT_TRIGGER = "CREATE OR REPLACE TRIGGER CHECK_BEFORE_INSERT_DDL " +
            "BEFORE INSERT ON DDL_SCRIPT_VERSION " +
            "FOR EACH ROW " +
            "DECLARE " +
            "    locked NUMBER; " +
            "BEGIN " +
            "    SELECT DB_LOCKED " +
            "    INTO locked " +
            "    FROM DDL_LOG_CONF " +
            "    WHERE UTENTE = ? AND PASSWORD = ?" +
            "    IF locked = 0 THEN " +
            "        RAISE_APPLICATION_ERROR(-20001, 'Insert not allowed: condition not met in other_table.'); " +
            "    END IF; " +
            "END ";
    String checkIfLoginFirstTime = "SELECT COUNT(*) FROM DDL_LOG_CONF";
    String checkLogin = "SELECT 1 FROM DDL_LOG_CONF WHERE UTENTE = ? AND PASSWORD = ?";
    String insertUser = "INSERT INTO DDL_LOG_CONF (UTENTE, PASSWORD, DB_LOCKED) VALUES (?, ?, 0)";
    String lockUnlockDB = "UPDATE DDL_LOG_CONF SET DB_LOCKED = ? WHERE UTENTE = ? AND PASSWORD = ?";
}
