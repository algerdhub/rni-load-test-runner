package main.java.runner;

import main.java.runner.utils.PropertyLoader;

public class Config {

    PropertyLoader propertyLoader = new PropertyLoader("application.properties");

    public final String PROJECT_ROOT_PATH = propertyLoader.loadProperty("project_root_path");
    public final String PROJECT_WORKSPACE_ROOT = propertyLoader.loadProperty("work_directory");
    public final String PROJECT_WORKSPACE_TEST_PLAN = propertyLoader.loadProperty("work_test_plan");
    public final String PROJECT_WORKSPACE_TEST_RESOUCE = propertyLoader.loadProperty("work_test_resource");
    public final String PROJJECT_WORKSPACE_TEST_RESULT = propertyLoader.loadProperty("work_test_result");

    public final String JMETER_ROOT_PATH = propertyLoader.loadProperty("jMeter_root_path");
    public final String JMETER_PROPERTIES_PATH = propertyLoader.loadProperty("jMeter_properties");
    public final String JMETER_EXECUTABLE_PATH = propertyLoader.loadProperty("jMeter_executable");
    public final String JMETER_SW_RESULTS_PATH = propertyLoader.loadProperty("jMeter_simple_writer_results");

    public Config(){
    }
}
