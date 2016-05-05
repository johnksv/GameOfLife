package gol.other;

import java.io.*;
import java.util.Properties;

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
     * Get the property from the configuration file. If the property is not
     * found, -1 will be returned.
     *
     * @param property the property you want to get
     * @return the value of the property. -1 if the property was not found.
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
     * Get the property as an int from the configuration file.
     *
     *
     * @param property the property you want to get
     * @return the property parsed as an integer. If no integer or property is
     * found, -1 will be returned.
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
        //Removes all non-digits
        prop = prop.replaceAll("\\D", "");
        
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
        properties.setProperty("gifWidth", "200");
        properties.setProperty("gifHeight", "200");
        properties.setProperty("gifSpeed", "500");
        properties.setProperty("startSize", "20");
        properties.setProperty("arrayLength", "200");
    }
}
