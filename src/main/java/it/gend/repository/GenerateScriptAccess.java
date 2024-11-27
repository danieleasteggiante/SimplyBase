package it.gend.repository;

import it.gend.domain.RecordDDL;
import it.gend.domain.ScriptVersion;
import it.gend.utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniele Asteggiante
 */
public class GenerateScriptAccess {

    public ScriptVersion getLastScriptVersion(Connection connection) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(GenerateScript_QUERY.GET_LAST_SCRIPT_VERSION);
            preparedStatement.setMaxRows(1);
            preparedStatement.setFetchSize(1);
            preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next())
                return null;
            return new ScriptVersion(
                    resultSet.getString("SOFTWARE"),
                    resultSet.getString("SOFTWARE_VERSION"),
                    resultSet.getDate("SCRIPT_DATE"),
                    resultSet.getString("CONTENT")
            );
        } catch (SQLException e) {
            System.err.println("Error during script generation " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<RecordDDL> getRecordDDLs(Connection connection, Date startDate) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(GenerateScript_QUERY.GET_RECORD_DDL);
            preparedStatement.setDate(1, startDate);
            preparedStatement.setString(2, PropertiesUtils.getProperty("db.user").toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next())
                return new ArrayList<>();
            List<RecordDDL> recordDDLs = new ArrayList<>();
            while (resultSet.next()) {
                recordDDLs.add(new RecordDDL(
                        resultSet.getDate("EVENT_DATE"),
                        resultSet.getString("ORA_SYSEVENT"),
                        resultSet.getString("ORA_DICT_OBJ_OWNER"),
                        resultSet.getString("ORA_DICT_OBJ_NAME"),
                        resultSet.getString("ORA_DICT_OBJ_TYPE"),
                        resultSet.getString("ORA_LOGIN_USER"),
                        resultSet.getString("DDL_TEXT_CLOB")
                ));
            }
            return recordDDLs;
        } catch (SQLException e) {
            System.err.println("Error during script generation " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
