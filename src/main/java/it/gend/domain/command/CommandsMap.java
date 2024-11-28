package it.gend.domain.command;

import java.util.Arrays;
import java.util.List;

/**
 * @author Daniele Asteggiante
 */
public enum CommandsMap {
    GENERATE_SCRIPT("generate","GenerateScriptDB", Arrays.asList("software", "version")),
    PRINT_SCRIPT("print","PrintScriptDB", Arrays.asList("header", "separator"));

    private final String commandName;
    private final String commandClassName;
    private final List<String> parametersAvailable;

    CommandsMap(String commandName, String commandClassName,List<String> parametersAvailable) {
        this.commandName = commandName;
        this.commandClassName = commandClassName;
        this.parametersAvailable = parametersAvailable;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandClassName() {
        return commandClassName;
    }

    public List<String> getParametersAvailable() {
        return parametersAvailable;
    }

    public static CommandsMap getCommandFromCommandName(String commandName) {
        for (CommandsMap command : CommandsMap.values()) {
            if (command.getCommandName().equals(commandName)) {
                return command;
            }
        }
        return null;
    }

    public static CommandsMap getCommandFromCommandClassName(String commandClassName) {
        for (CommandsMap command : CommandsMap.values()) {
            if (command.getCommandClassName().equals(commandClassName)) {
                return command;
            }
        }
        return null;
    }
}
