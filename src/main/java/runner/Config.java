package main.java.runner;

import main.java.runner.utils.PropertyLoader;

public class Config {

    PropertyLoader propertyLoader = new PropertyLoader("application.properties");

    public final String PROJECT_ROOT_PATH = propertyLoader.loadProperty("project_root_path");
    public final String PROJECT_WORKSPACE_ROOT = propertyLoader.loadProperty("work_directory");
    public final String PROJECT_TEST_PLAN = propertyLoader.loadProperty("work_test_plan");
    public final String PROJECT_TEST_RESOUCE = propertyLoader.loadProperty("work_test_resource");
    public final String PROJJECT_TEST_RESULT = propertyLoader.loadProperty("work_test_result");

    public final String JMETER_ROOT_PATH = propertyLoader.loadProperty("jMeter_root_path");
    public final String JMETER_PROPERTIES_PATH = propertyLoader.loadProperty("jMeter_properties");
    public final String JMETER_EXECUTABLE_PATH = propertyLoader.loadProperty("jMeter_executable");

    public final String JMETER_TOTAL_RESULTS_PATH = propertyLoader.loadProperty("jMeter_total_results");
    public final String JMETER_SUMMARY_RESULTS_PATH = propertyLoader.loadProperty("jMeter_summary_results");
    public final String TRANSACTION_CONTROLLER_PREFIX = propertyLoader.loadProperty("transaction_controller_sapmler_prefix");

    public final String LOADOSOPHIA_ADDRESS = propertyLoader.loadProperty("loadosophia_address");
    public final String LOADOSOPHIA_TITLE = propertyLoader.loadProperty("loadosophia_title");
    public final String LOADOSOPHIA_TOKEN = propertyLoader.loadProperty("loadosophia_token");
    public final String LOADOSOPHIA_PROJECT = propertyLoader.loadProperty("loadosophia_project");

    public Config(){
    }
}
