package main.java.runner.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.*;

public class RfileUtils {
    private static Logger log = Logger.getLogger(RfileUtils.class.getName());

    public static void createFolder(String path){
        File file = new File(path);
        if(!file.exists()){
            log.info("Create folder: " + path);
            file.mkdirs();
        }
    }

    public static void deleteFolder(String path){
        try {
            log.info("Delete folder: " + path);
           FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error when deleting folder: " + path, e);
        }
    }

    public static String combinePaths(String root, String... more){
        Path path = Paths.get(root, more);
        return path.toString();
    }

}
