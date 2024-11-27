package it.gend.controller;

import it.gend.connection.JDBCConnector;
import it.gend.domain.command.Command;
import it.gend.repository.CheckAccess;
import it.gend.repository.DDL_QUERY;
import it.gend.utils.PrintUtils;
import it.gend.utils.PropertiesUtils;

import java.sql.Connection;
import java.util.Arrays;

/**
 * @author Daniele Asteggiante
 */

public class MainController {
    Connection connection;
    CheckAccess checkAccess;

    public void start() {
        PrintUtils.printHeader("SimplyBase");
        connection = JDBCConnector.getConnection();
        checkOrCreateTablesAndTriggerExists();
        loginAccess();
    }

    public Command[] decode(String[] args) {
        try {
            System.out.println("Decoding commands...");
            Command[] commands = new Command[args.length];
            for (int i = 0; i < args.length; i++) {
                Command command = Class.forName("it.gend.domain.command." + args[i]).asSubclass(Command.class).newInstance();
                commands[i] = command;
            }
            return commands;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.err.println("Error during command decoding " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        System.out.println("System logout...");
        checkAccess.lockUnlockDB(connection, DDL_QUERY.lockUnlockDB, 0);
        JDBCConnector.closeConnection(connection);
    }

    private void loginAccess() {
        System.out.println("System login...");
        String user = PropertiesUtils.getProperty("user");
        String password = PropertiesUtils.getProperty("password");
        boolean recordIsPresent = checkAccess.countRecord(connection, DDL_QUERY.checkIfLoginFirstTime);
        if (!recordIsPresent) {
            System.out.println("First time login, creating user...");
            checkAccess.addUser(connection, user, password, DDL_QUERY.insertUser);
        }
        boolean login = checkAccess.login(connection, user, password, DDL_QUERY.checkLogin);
        if (!login) {
            System.out.println("Login failed, exiting...");
            System.exit(0);
        }
        checkAccess.lockUnlockDB(connection, DDL_QUERY.lockUnlockDB, 1);
    }

    private void checkOrCreateTablesAndTriggerExists() {
        System.out.println("Checking if tables exists...");
        checkAccess = new CheckAccess();
        boolean DDL_LOG_CONF_exists = checkAccess.findObject(connection, "DDL_LOG_CONF", DDL_QUERY.checkTableExistence);
        if (!DDL_LOG_CONF_exists) {
            System.out.println("Table DDL_LOG_CONF not found, creating it...");
            checkAccess.executeDDLQuery(connection, DDL_QUERY.DDL_LOG_CONF);
        }
        boolean DDDL_SCRIPT_VERSION_exists = checkAccess.findObject(connection, "DDL_SCRIPT_VERSION", DDL_QUERY.checkTableExistence);
        if (!DDDL_SCRIPT_VERSION_exists) {
            System.out.println("Table DDL_SCRIPT_VERSION not found, creating it...");
            checkAccess.executeDDLQuery(connection, DDL_QUERY.DDL_SCRIPT_VERSION);
        }
        boolean DDL_LOG_exists = checkAccess.findObject(connection, "DDL_LOG", DDL_QUERY.checkTableExistence);
        if (!DDL_LOG_exists) {
            System.out.println("Table DDL_LOG not found, creating it...");
            checkAccess.executeDDLQuery(connection, DDL_QUERY.DDL_LOG);
        }
        boolean DDL_TRIGGER_exists = checkAccess.findObject(connection, "LOG_DDL", DDL_QUERY.checkTriggerExistence);
        if (!DDL_TRIGGER_exists) {
            System.out.println("Trigger DDL_TRIGGER not found, creating it...");
            checkAccess.executeDDLQuery(connection, DDL_QUERY.DDL_TRIGGER);
        }
        boolean DDL_INSERT_TRIGGER_exists = checkAccess.findObject(connection, "CHECK_BEFORE_INSERT_DDL", DDL_QUERY.checkTriggerExistence);
        if (!DDL_INSERT_TRIGGER_exists) {
            System.out.println("Trigger DDL_INSERT_TRIGGER not found, creating it...");
            checkAccess.executeDDLQuery(connection, DDL_QUERY.DDL_INSERT_TRIGGER);
        }
    }

    public void perform(Command[] commands) {
        System.out.println("Performing commands..." + Arrays.toString(commands));
        for (Command command : commands) {
            System.out.println(command);
        }
    }
}
