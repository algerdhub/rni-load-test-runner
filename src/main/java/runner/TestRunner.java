package main.java.runner;

import main.java.runner.utils.RfileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.*;

import main.java.runner.utils.RuntimeRunner;
import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

public class TestRunner {

    private static Logger log = Logger.getLogger(TestRunner.class.getName());

    public static void main(String[] args){
        log.info("--Start");

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

        Subparser result = subparsers.addParser("prepare_results").help("Process result .csv files");
        result.addArgument("--clear_trans_samplers")
                .type(Boolean.class)
                .setDefault(true)
                .help("Remove transaction controllers from results");

        Subparser loadosophia = subparsers.addParser("loadosophia").help("Upload results to Loadosophia.org");
        loadosophia.addArgument("--target_file")
                .type(Arguments.fileType().verifyIsFile())
                .required(true)
                .help("Define a file that will be a target file for Loadosophia.org");
        loadosophia.addArgument("--other_files")
                .type(Arguments.fileType().verifyIsFile())
                .required(false)
                .nargs(1)
                .help("Define other files that needs to be uploaded to Loadosophia.org");

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
        } else if(command.equals("prepare_results")){
            prepareResults(config, res);
        } else if (command.equals("loadosophia")){
            uploadResults(config, res);
        }

        //
        log.info("--Exit");
    }

    private static void run(Config config, Namespace commands){
        prepareWorkSpace(config, commands);
        start(config, commands);
    }

    private static void prepareWorkSpace(Config config, Namespace attributes) {
        log.info("Prepare test workspace");
        //clear workspace before starting new test
        RfileUtils.deleteFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT));

        String workTestPlanDir = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_TEST_PLAN);
        String workSourcesDir = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_TEST_RESOUCE);
        RfileUtils.createFolder(workTestPlanDir);
        RfileUtils.createFolder(workSourcesDir);
        RfileUtils.createFolder(RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_TEST_RESULT));

        //copy test plan
        RfileUtils.copyFile(attributes.get("test_plan").toString(), workTestPlanDir);

        //copy sources
        String staticSourcesDir = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_TEST_RESOUCE);
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
                RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJECT_TEST_PLAN, testName),
                RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_TEST_RESULT, config.JMETER_TOTAL_RESULTS_PATH));
        log.info("Run command: " + command);
        RuntimeRunner.execute(command, config);
    }

    private static void prepareResults(Config config, Namespace attributes){
        String totalResults = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_TEST_RESULT, config.JMETER_TOTAL_RESULTS_PATH);
        String summaryResults = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJECT_WORKSPACE_ROOT, config.PROJJECT_TEST_RESULT, config.JMETER_SUMMARY_RESULTS_PATH);
        List totalList = RfileUtils.readCSV(totalResults);
        List summaryList = RfileUtils.readCSV(summaryResults);
        if(attributes.get("clear_trans_samplers")){
            totalList = RfileUtils.clearTransactionSamplers(totalList, config.TRANSACTION_CONTROLLER_PREFIX);
            summaryList = RfileUtils.clearTransactionSamplers(summaryList, config.TRANSACTION_CONTROLLER_PREFIX);
        }

        String finalPath = RfileUtils.combinePaths(config.PROJECT_ROOT_PATH, config.PROJJECT_TEST_RESULT);
        RfileUtils.writeCSV(totalList, RfileUtils.combinePaths(finalPath, config.JMETER_TOTAL_RESULTS_PATH));
        RfileUtils.writeCSV(summaryList, RfileUtils.combinePaths(finalPath, config.JMETER_SUMMARY_RESULTS_PATH));
    }

    private static void uploadResults(Config config, Namespace attributes){
        String targetFile = attributes.get("target_file").toString();
        ArrayList list = null;
        LinkedList<String> addFiles = new LinkedList<String>();
        try{
            list = attributes.get("other_files");
        } catch (NullPointerException ex){
            //do nothing
        }

        if(list != null && list.size() > 0){
            Iterator<String> iterator = list.iterator();
            String line;
            while (iterator.hasNext()){
                line = String.valueOf(iterator.next());
                addFiles.add(line.toString());
            }
        } else {
            log.info("There are no additional files to upload to Loadosophia.org");
        }
        LoadosophiaUploader uploader = new LoadosophiaUploader(config);
        try {
            uploader.load(targetFile, addFiles);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to upload files to Loadosophia.org", e);
        }
    }

    private static Config getConfig(){
        return new Config();
    }
}
