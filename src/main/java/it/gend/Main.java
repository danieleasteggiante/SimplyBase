package it.gend;

import it.gend.controller.MainController;
import it.gend.domain.Arg;

import java.util.List;
import java.util.Map;

/**
 * @author Daniele Asteggiante
 */
public class Main {
    public static void main(String[] args) {
        MainController mainController = new MainController();
        mainController.start();
        Map<String, List<Arg>> commands = mainController.decode(args);
        mainController.perform(commands);
        mainController.stop();
    }
}