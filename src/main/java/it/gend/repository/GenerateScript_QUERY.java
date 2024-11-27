package it.gend.repository;

/**
 * @author Daniele Asteggiante
 */
public interface GenerateScript_QUERY {
    String GET_LAST_SCRIPT_VERSION = "SELECT * FROM DDL_SCRIPT_VERSION ORDER BY SCRIPT_DATE DESC LIMIT 1";
    String GET_RECORD_DDL = "SELECT * FROM DDL_SCRIPT WHERE EVENT_DATE >= ? AND ORA_DICT_JOB_OWNER = ? ORDER BY EVENT_DATE ASC";
}
