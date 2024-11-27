package it.gend.utils;

import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

/**
 * @author Daniele Asteggiante
 */
public class PrintUtils {
    public static void printHeader(String message) {
        try {
            String version = PropertiesUtils.getProperty("version");
            String asciiArt = FigletFont.convertOneLine(message + " " + version);
            System.out.println(asciiArt);
        } catch (IOException e) {
            System.err.println("Error during ASCII Art creation " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
