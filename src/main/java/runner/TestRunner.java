package main.java.runner;

import main.java.runner.utils.RfileUtils;
import java.util.logging.*;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

public class TestRunner {

    private static Logger log = Logger.getLogger(TestRunner.class.getName());

    public static void main(String[] args){
        log.info("--Start Test");


        //
        ArgumentParser parser = ArgumentParsers.newArgumentParser("rni")
                .description("Process rni load testing");
        Subparsers subparsers = parser.addSubparsers().help("sub-command help");

        Subparser run = subparsers.addParser("run").help("run command");
        run.addArgument("--test")
                .type(String.class)
                .required(true);

        try {
            Namespace res = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

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
