package main.java.runner;

import main.java.runner.utils.RfileUtils;

import java.io.File;
import java.util.logging.*;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

public class TestRunner {

    private static Logger log = Logger.getLogger(TestRunner.class.getName());

    public static void main(String[] args){
        log.info("--Start Test");

        log.info("Read application config");
        Config config = getConfig();
        //
        ArgumentParser parser = ArgumentParsers.newArgumentParser("rni")
                .description("Process rni load testing");
        Subparsers subparsers = parser.addSubparsers().help("");

        Subparser run = subparsers.addParser("run").help("Run test plan");
        run.addArgument("--test_plan")
                .type(Arguments.fileType().verifyIsFile())
                .required(true)
                .help("Path to the test plan");
        run.addArgument("--set_plan")
                .type(Boolean.class)
                .required(false)
                .setDefault(false)
                .help("If true, test plan .jmx will be edited with parameters set in test plan config");

        Subparser status = subparsers.addParser("status").help("Check test execution status");

        Subparser result = subparsers.addParser("result").help("Process result .csv files");


        Namespace res = null;
        try {
            res = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        String command = args[0];
        if(command == null){
            log.log(Level.SEVERE, "Missing command");
            System.exit(1);
        } else if(command.equals("run")){
            run(config, res);
        } else if(command.equals("status"))
        {
            //TODO: status method
        } else if(command.equals("result")){
            //TODO: result method
        }

        //
        log.info("--Test is completed");
    }

    private static void run(Config config, Namespace commands){
        prepareWorkSpace(config, commands);
        start(config, commands);
    }

    private static void prepareWorkSpace(Config config, Namespace attributes) {
        log.info("Prepare test workspace");
        //clear workspace before starting new test
        RfileUtils.deleteFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT));

        String workTestPlanDir = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_WORKSPACE_TEST_PLAN);
        String workSourcesDir = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_WORKSPACE_TEST_RESOUCE);
        RfileUtils.createFolder(workTestPlanDir);
        RfileUtils.createFolder(workSourcesDir);
        RfileUtils.createFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_WORKSPACE_TEST_RESULT));

        //copy test plan
        RfileUtils.copyFile(attributes.get("test_plan").toString(), workTestPlanDir);

        //copy sources
        String staticSourcesDir = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_TEST_RESOUCE);
        RfileUtils.copyFiles(staticSourcesDir, workSourcesDir, new String[]{"csv"});

        //copy fixed jmeter.properties file to bin folder (replace existing)
        String jMeterBinDir = RfileUtils.combinePaths(config.JMETER_ROOT_PATH, "bin");
        String propFile = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.JMETER_PROPERTIES_PATH);
        RfileUtils.copyFile(propFile, jMeterBinDir);
    }

    private static void start(Config config, Namespace attributes){
        String testName = RfileUtils.getFileName(attributes.get("test_plan").toString());
        String command = String.format("%s -n -t %s -l %s",
                config.JMETER_EXECUTABLE_PATH,
                RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_WORKSPACE_TEST_PLAN, testName),
                RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_WORKSPACE_TEST_RESULT, config.JMETER_SW_RESULTS_PATH));
        log.info("Run command: " + command);
    }

    private static Config getConfig(){
        return new Config();
    }
}
