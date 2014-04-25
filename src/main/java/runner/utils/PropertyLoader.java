package main.java.runner.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {

    private final String prop_file;

    public PropertyLoader(String filename)
    {
        this.prop_file = filename;
    }

    public String loadProperty(String propertyName){
        Properties props = new Properties();
        try {
            props.load(PropertyLoader.class.getResourceAsStream(prop_file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String value = "";

        if (propertyName != null) {
            value = props.getProperty(propertyName);
        }
        return value;
    }
}
