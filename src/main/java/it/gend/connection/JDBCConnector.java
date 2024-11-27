package it.gend.connection;

import it.gend.utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Daniele Asteggiante
 */
public class JDBCConnector {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null)
            return connection;
        try {
            String jdbcUrl = PropertiesUtils.getProperty("db.jdbcUrl");
            String user = PropertiesUtils.getProperty("db.user");
            String password = PropertiesUtils.getProperty("db.password");
            String name = PropertiesUtils.getProperty("db.name");
            connection = DriverManager.getConnection(jdbcUrl + "/" + name, user, password);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            System.err.println("Errore di connessione al database " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            System.err.println("Errore durante la chiusura della connessione " + e.getMessage());
        }
    }
}
