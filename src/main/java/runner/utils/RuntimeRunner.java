package main.java.runner.utils;

import main.java.runner.Config;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuntimeRunner {
    private static Logger log = Logger.getLogger(RuntimeRunner.class.getName());

    public static void execute(String command, Config config){
        try {
            log.info("The test has been running");
            String path = RfileUtils.combinePaths(config.JMETER_ROOT_PATH, "bin");
            File dir = new File(path);

            Process process = Runtime.getRuntime().exec(command,
                    null, dir);
            final int returnCode = process.waitFor();
            log.info("The test has completed");

    } catch (Exception ex) {
        log.log(Level.SEVERE, "Could not start process: " + command + ".", ex);
    }

    }
}
