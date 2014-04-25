package main.java.runner;

import main.java.runner.utils.PropertyLoader;

public class TestRunner {
    public static void main(String[] args){
        System.out.println("dsf");
        PropertyLoader propertyLoader = new PropertyLoader("application.properties");
        try {
            System.out.println(propertyLoader.loadProperty("jMeter_master_root"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
