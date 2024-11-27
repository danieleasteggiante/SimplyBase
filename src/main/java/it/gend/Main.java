package it.gend;

import it.gend.controller.MainController;
import it.gend.domain.command.Command;

/**
 * @author Daniele Asteggiante
 */
public class Main {
    public static void main(String[] args) {
        MainController mainController = new MainController();
        mainController.start();
        Command[] commands = mainController.decode(args);
        mainController.perform(commands);
        mainController.stop();
    }
}