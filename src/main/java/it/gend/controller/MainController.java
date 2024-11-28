package it.gend.controller;

import it.gend.connection.JDBCConnector;
import it.gend.domain.Arg;
import it.gend.domain.command.Command;
import it.gend.domain.command.CommandsMap;
import it.gend.repository.CheckAccess;
import it.gend.repository.DDL_QUERY;
import it.gend.utils.PrintUtils;
import it.gend.utils.PropertiesUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, List<Arg>> decode(String[] args) {
        try {
            System.out.println("Decoding commands...");
            Map<String, List<Arg>> commandsMap = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                CommandsMap command = CommandsMap.getCommand(args[i]);
                if (command == null) {
                    System.err.println("Command not found: " + args[i]);
                    throw new RuntimeException("Command not found: " + args[i]);
                }
                commandsMap.put(command.getCommandClassName(), new ArrayList<>());
                while (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    String[] arg = args[++i].split("=");
                    if (!parameterExistInCommand(arg[0], command))
                        throw new RuntimeException("Argument not found in command " + command.getCommandClassName() + " : " + arg[0]);
                    commandsMap.get(command.getCommandClassName()).add(new Arg(arg[0], arg[1]));
                    i++;
                }
            }
            return commandsMap;
        } catch (Exception e) {
            System.err.println("Error during command decoding " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private boolean parameterExistInCommand(String s, CommandsMap command) {
        return command.getParametersAvailable().contains(s);
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

    public void perform(Map<String, List<Arg>> commands) {
        try {
            System.out.println("Performing commands...");
            for (String command : commands.keySet()) {
                Command commandInstance = Class.forName(command).asSubclass(Command.class).newInstance();
                List<String> args = orderArgs(commands.get(command));
                commandInstance.execute(args.toArray(new String[0]));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.err.println("Error during command execution " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<String> orderArgs(List<Arg> args) {
        List<String> list = new ArrayList<>();
        CommandsMap commandMap = CommandsMap.getCommand(args.get(0).getName());
        if (commandMap == null)
            throw new RuntimeException("Command not found: " + args.get(0).getName());
        for (String parameter : commandMap.getParametersAvailable()) {
            for (Arg arg : args) {
                if (arg.getName().equals(parameter)) {
                    list.add(arg.getValue());
                    break;
                }
            }
        }
        return list;
    }
}
