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

    /**
     * //TODO JAvadoc
     *
     * @param property
     * @return
     */
    public static String getProp(String property) {
        String prop = properties.getProperty(property, "-1").toLowerCase();
        if (prop.equals("-1")) {
            makePropFile();
            return properties.getProperty(property, "-1").toLowerCase();
        } else {
            return prop;
        }
    }

    /**
     * Configurations can not contain negative values!
     *
     * @param property
     * @return
     */
    public static int getPropInt(String property) {
        String prop = properties.getProperty(property, "-1").toLowerCase();
        if (prop.equals("-1")) {
            makePropFile();
        }

        prop = properties.getProperty(property, "-1").toLowerCase();
        if (prop.equals("-1")) {
            return -1;
        }
        prop = prop.replaceAll("\\D", "").trim();
        
        if (prop.equals("")) {
            return -1;
        } else {
            return Integer.parseInt(prop);
        }

    }

    private static void makePropFile() {
        //Nasty, but has to be done
        properties.setProperty("dynamicBoard", "true");
        properties.setProperty("maxWidth", "200");
        properties.setProperty("maxHeight", "200");
        properties.setProperty("expansion", "50");
        properties.setProperty("useThreads", "true");
        properties.setProperty("arrayBoardThreshold", "500");
        properties.setProperty("gifWidth", "200");
        properties.setProperty("gifHeight", "200");
    }
}
