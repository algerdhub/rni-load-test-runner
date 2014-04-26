package main.java.runner.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
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
            log.log(Level.SEVERE, "Could not delete folder: " + path, e);
            System.exit(1);
        }
    }

    public static String combinePaths(String root, String... more){
        Path path = Paths.get(root, more);
        return path.toString();
    }

    public static void copyFile(String fileScr, String targetDir){
        File file = new File(fileScr);
        File dir = new File(targetDir);
        log.info("Copy file " + file + " to " + dir);
        try {
            FileUtils.copyFileToDirectory(file, dir);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not copy file " + file + " to " + dir, e);
            System.exit(1);
        }
    }

    public static void copyFiles(Collection<File> filesList, String targetDir){
        for(File file : filesList){
            copyFile(file.toString(), targetDir);
        }
    }

    public static void copyFiles(String sourceDir, String targetDir, String[] extensions){
        File source = new File(sourceDir);
        log.info("Get files list in folder: " + source);
        Collection<File> listOfFiles = FileUtils.listFiles(source, extensions, false);
        copyFiles(listOfFiles, targetDir);
    }

    public static String getFileName(String path){
        return FilenameUtils.getName(path);
    }


}
