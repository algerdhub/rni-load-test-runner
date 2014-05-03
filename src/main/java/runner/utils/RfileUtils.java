package main.java.runner.utils;

import java.awt.*;
import java.io.*;
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

    public static List readCSV(String file){
        BufferedReader br = null;
        String line = null;
        List result = new List();
        log.info("Read csv file: " + file);
        try{
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null){
                result.add(line);
            }
            br.close();
        }
        catch (FileNotFoundException ex){
            log.warning("Results file not found: " + file);
            return null;
        }
        catch (IOException ex){
            log.warning("Error while reading results file: " + file);
            return null;
        }
        return result;
    }

    public static List clearTransactionSamplers(List list, String prefix){
        if(list == null){
            return null;
        }
        for (String line : list.getItems()){
            if(line.startsWith(prefix)){
                list.remove(line);
            }
        }
        return list;
    }

    public static void writeCSV(List list, String file){
        if (list == null){
            log.warning("Empty results list. Nothing to write to results scv: " + file);
            return;
        }
        BufferedWriter bw = null;
        log.info("Write csv file: " + file);

        try{
            bw = new BufferedWriter(new FileWriter(file));
            for (String line : list.getItems()){
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException ex) {
            log.warning("Could not write file: " + file);
        }
    }


}
