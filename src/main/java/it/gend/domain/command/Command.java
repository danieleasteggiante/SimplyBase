package it.gend.domain.command;

/**
 * @author Daniele Asteggiante
 * 2 ruole di pizzette 1 di focccia col prosciutto, una di focaccia con la mortadella,
 * 1 di salatini.
 */
public interface Command {
    void execute(String... args);
}
