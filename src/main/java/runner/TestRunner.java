package main.java.runner;

import main.java.runner.utils.RfileUtils;
import java.util.logging.*;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

public class TestRunner {

    private static Logger log = Logger.getLogger(TestRunner.class.getName());

    public static void main(String[] args){
        log.info("--Start Test");


        //
        ArgumentParser parser = ArgumentParsers.newArgumentParser("rni")
                .description("Process rni load testing");
        Subparsers subparsers = parser.addSubparsers().help("");

        Subparser run = subparsers.addParser("run").help("Run test plan");
        run.addArgument("--test_plan")
                .type(Arguments.fileType().verifyIsFile())
                .required(true)
                .help("Path to the test plan");
        run.addArgument("--update_test_plan_via_config")
                .type(Boolean.class)
                .required(false)
                .setDefault(false)
                .help("If true test plan will be edited with parameters set in test plan config");
        run.addArgument("--test")
                .type(Boolean.class)
                .required(false)
                .help("test");

        Subparser status = subparsers.addParser("status").help("Check test execution status");
        status.addArgument("--ss")
                .type(String.class);

        Namespace res = null;
        try {
            res = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        String command = args[0];
        System.out.println(res.getAttrs().get("test_plan"));

        //



        prepareToRun();
        log.info("--Test is completed");
    }

    private static void prepareToRun() {
        Config config = new Config();

        RfileUtils.deleteFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT));

        RfileUtils.createFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_WORKSPACE_TEST_PLAN));
        RfileUtils.createFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_WORKSPACE_TEST_RESOUCE));
        RfileUtils.createFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_WORKSPACE_TEST_RESULT));


    }
}
