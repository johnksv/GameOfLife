package gol.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s305054, s305089, s305084
 */
public class Configuration {

    private static final Properties properties = new Properties();
    private static final File configFile = new File("src\\gol\\other\\config.properties");

    public static void loadConfig() {
        try {
            FileInputStream input = new FileInputStream(configFile);
            properties.load(input);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getProp(String property) {
        return properties.getProperty(property, "-1").toLowerCase();
    }

}
