package gol.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for reading from properties file.
 *
 * @author s305054, s305089, s305084
 */
public class Configuration {

    private static final Properties properties = new Properties();
    private static File configFile = new File("src/gol/other/config.properties");

    /**
     * Initialize the properties file into the program.
     */
    public static void loadConfig() {
        try {
            FileInputStream input = new FileInputStream(configFile);
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("There was an error reading the properties file."
                    + " It was sat to default values.");
            makePropFile();
        }
    }

    public static String getProp(String property) {
        return properties.getProperty(property, "-1").toLowerCase();
    }

    private static void makePropFile() {
        //Nasty, but has to be done
        properties.setProperty("dynamicBoard", "true");
        properties.setProperty("maxWidth", "200");
        properties.setProperty("maxHeight", "200");
        properties.setProperty("expansion", "50");
        properties.setProperty("useThreads", "true");
        properties.setProperty("arrayBoardThreshold", "500");
    }
}
