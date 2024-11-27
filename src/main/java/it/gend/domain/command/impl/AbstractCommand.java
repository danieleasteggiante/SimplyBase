package it.gend.domain.command.impl;

import it.gend.domain.command.Command;

/**
 * @author Daniele Asteggiante
 */
public abstract class AbstractCommand implements Command {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
