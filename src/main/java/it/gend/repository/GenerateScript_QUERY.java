package it.gend.repository;

/**
 * @author Daniele Asteggiante
 */
public interface GenerateScript_QUERY {
    String GET_LAST_SCRIPT_VERSION = "SELECT * FROM DDL_SCRIPT_VERSION ORDER BY SCRIPT_DATE DESC FETCH FIRST ROW ONLY";
    String GET_RECORD_DDL = "SELECT * FROM DDL_LOG WHERE EVENT_DATE >= ? AND ORA_DICT_OBJ_OWNER = ? ORDER BY EVENT_DATE ASC";
    String SAVE_SCRIPT_VERSION = "INSERT INTO DDL_SCRIPT_VERSION (SOFTWARE, SOFTWARE_VERSION, SCRIPT_DATE, CONTENT) VALUES (?, ?, ?, ?)";
}
