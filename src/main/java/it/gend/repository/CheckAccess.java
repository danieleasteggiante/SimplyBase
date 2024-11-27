package it.gend.repository;

import it.gend.utils.PropertiesUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Daniele Asteggiante
 */
public class CheckAccess {
    private Connection connection;

    public boolean findObject(Connection connection, String objectName, String query) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, objectName.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            System.err.println("Error during table check " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void executeDDLQuery(Connection connection, String ddlQuery) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(ddlQuery);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            System.err.println("Error during table creation " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean countRecord(Connection connection, String checkIfLoginFirstTime) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(checkIfLoginFirstTime);
            ResultSet resultSet = preparedStatement.executeQuery();
            int result = resultSet.next() ? resultSet.getInt(1) : 0;
            return result > 0;
        } catch (Exception e) {
            System.err.println("Error during record count " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean login(Connection connection, String user, String password, String checkLogin) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(checkLogin);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            System.err.println("Error during login " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addUser(Connection connection, String user, String password, String insertUser) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(insertUser);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            System.err.println("Error during user creation " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void lockUnlockDB(Connection connection, String lockUnlockDB, int i) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(lockUnlockDB);
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, PropertiesUtils.getProperty("user"));
            preparedStatement.setString(3, PropertiesUtils.getProperty("password"));
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            System.err.println("Error during lock/unlock " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
