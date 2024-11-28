package it.gend.domain;

/**
 * @author Daniele Asteggiante
 */
public class Arg {
    String name;
    String value;

    public Arg(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
