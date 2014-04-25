package main.java.runner.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {

    private final File prop_file;

    public PropertyLoader(String filename)
    {
        this.prop_file = new File(filename);
    }

    public String loadProperty(String propertyName) throws FileNotFoundException {
        Properties props = new Properties();
        FileReader reader = new FileReader(prop_file);
        try {
            props.load(reader);
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
