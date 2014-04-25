package main.java.runner.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyLoader {

    private static Logger log = Logger.getLogger(PropertyLoader.class.getName());
    private final String prop_file;

    public PropertyLoader(String filename)
    {
        this.prop_file = filename;
    }

    public String loadProperty(String propertyName){
        Properties props = new Properties();
        FileReader reader = null;
        String value = "";
        try {
            reader = new FileReader(prop_file);
            props.load(reader);

            if (propertyName != null) {
                log.info("Loading property with name: " + propertyName);
                value = props.getProperty(propertyName);
            }

        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Config file " + prop_file + " not found", e);
        }
          catch (IOException e) {
            log.log(Level.SEVERE, "Error when loading properties", e);
        }
          catch (NullPointerException e) {
            log.log(Level.SEVERE, "Could not read property", e);
    }
        return value;
    }
}
