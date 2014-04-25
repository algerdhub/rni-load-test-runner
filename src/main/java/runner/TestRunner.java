package main.java.runner;

import main.java.runner.utils.PropertyLoader;

import java.io.FileNotFoundException;

public class TestRunner {
    public static void main(String[] args){
        System.out.println("dsf");
        PropertyLoader propertyLoader = new PropertyLoader("application.properties");
        try {
            System.out.println(propertyLoader.loadProperty("test"));
            System.out.println(propertyLoader.loadProperty("notest"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
