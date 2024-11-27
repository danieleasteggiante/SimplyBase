package it.gend.domain;

import it.gend.configuration.Event;
import it.gend.configuration.ObjType;

/**
 * @author Daniele Asteggiante
 */
public class ScriptElement {
    private String name;
    private ObjType type;
    private Event event;
    private String line;
    private int lineNumber;

    public ScriptElement(String name, ObjType type, Event event, String line, int lineNumber) {
        this.name = name;
        this.type = type;
        this.event = event;
        this.line = line;
        this.lineNumber = lineNumber;
    }

    public String getName() {
        return name;
    }

    public ObjType getType() {
        return type;
    }

    public Event getEvent() {
        return event;
    }

    public String getLine() {
        return line;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
