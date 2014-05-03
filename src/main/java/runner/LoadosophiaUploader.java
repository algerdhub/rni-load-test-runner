package main.java.runner;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;


public class LoadosophiaUploader {
    private static Logger log = Logger.getLogger(LoadosophiaUploader.class.getName());
    private String address;
    private String title;
    private String token;
    private String project;
    private final String STATUS_DONE = "4";

    public LoadosophiaUploader(Config config) {
        address = config.LOADOSOPHIA_ADDRESS;
        token = config.LOADOSOPHIA_TOKEN;
        project = config.LOADOSOPHIA_PROJECT;
        title = config.LOADOSOPHIA_TITLE;
    }

    public void load(String targetFile, LinkedList<String> additionalFiles) throws IOException{
        try {
            int queueId = sendFilesToLoadosophia(new File(targetFile), additionalFiles);
            String results;
            if(!title.trim().isEmpty()){
                int testId = getTestByUpload(queueId);
                setTestTitle(testId, title.trim());
                results = address + "/gui/" + testId + "/";
            }  else {
                results = address + "/api/file/status/" + queueId + "/?redirect=true";
            }
            log.info("Uploaded successfully, go to results: " + results);
        } catch (IOException ex){
             log.log(Level.SEVERE, "Failed to upload results to loadosophia", ex);
        }
    }

    private int sendFilesToLoadosophia(File targetFile, LinkedList<String> otherFiles) throws IOException{
         if(targetFile.length() == 0){
             throw new IOException("Cannot send empty file to Loadosophia.org");
         }
        log.info("Preparing files to send");
        LinkedList<Part> partList = new LinkedList<Part>();
        partList.add(new StringPart("projecKey", project));
        File targetZipFile = gzipFile(targetFile);
        partList.add(new FilePart("jtl_file", new FilePartSource(targetZipFile)));

        Iterator<String> it = otherFiles.iterator();
        int index = 0;
        ArrayList<File> otherZipFiles = new ArrayList<File>();
        while (it.hasNext()){
            File otherFile = new File(it.next());
            if(!otherFile.exists()){
                log.warning("File does not exist. Skipping: " + otherFile.getAbsolutePath());
                continue;
            }
            File otherZipFile = gzipFile(otherFile);
            otherZipFiles.add(otherZipFile);
            partList.add(new FilePart("perfmon_" + index, new FilePartSource(otherZipFile)));
            index++;
        }
        log.info("Start uploading to Loadosophia.org");
        String[] fields = doRequest(partList, getUploaderURI(), HttpStatus.SC_OK);
        targetZipFile.delete();
        for (File file : otherZipFiles){
            file.delete();
        }
        return Integer.parseInt(fields[0]);
    }

    private File gzipFile(File src) throws IOException{
        String outFileName = src.getAbsolutePath() + ".gz";
        log.info("Gzipping file: " + src.getAbsolutePath());
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFileName));

        FileInputStream in = new FileInputStream(src);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
        }
        in.close();

        out.finish();
        out.close();

        return new File(outFileName);
    }

    private String[] doRequest(LinkedList<Part> parts, String URL, int expectedSC) throws IOException{
        log.info("Request " + URL);
        parts.add(new StringPart("token", token));

        HttpClient uploader = new HttpClient();
        PostMethod postRequest = new PostMethod(URL);
        MultipartRequestEntity multipartRequest = new MultipartRequestEntity(parts.toArray(new Part[0]), postRequest.getParams());
        postRequest.setRequestEntity(multipartRequest);
        int result = uploader.executeMethod(postRequest);
        if (result != expectedSC) {
            String fName = File.createTempFile("error_", ".html").getAbsolutePath();
            log.info("Saving server error response to: " + fName);
            FileOutputStream fos = new FileOutputStream(fName);
            FileChannel resultFile = fos.getChannel();
            resultFile.write(ByteBuffer.wrap(postRequest.getResponseBody()));
            resultFile.close();
            HttpException $e = new HttpException("Request returned not " + expectedSC + " status code: " + result);
            throw $e;
            }
        byte[] bytes = postRequest.getResponseBody();
        if (bytes == null) {
            bytes = new byte[0];
            }
        String response = new String(bytes);
        String[] fields = response.trim().split(";");
        return fields;
    }

    private String getUploaderURI(){
        return address + "/api/file/upload/?format=csv";
    }

    private String[] getUploadStatus(int queueId) throws IOException{
          String uri = address + "/api/file/status/" + queueId + "/?format=csv";
          return doRequest(new LinkedList<Part>(), uri, HttpStatus.SC_OK);
    }

    private void setTestTitle(int testId, String trim) throws IOException{
        String uri = address + "/api/test/edit/title/" + testId + "/?title=" + URLEncoder.encode(trim, "UTF-8");
        doRequest(new LinkedList<Part>(), uri, HttpStatus.SC_NO_CONTENT);
    }

    private int getTestByUpload(int queueId) throws IOException {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex){
                throw new RuntimeException("Failed to get Test ID");
            }
            String[] status = getUploadStatus(queueId);
            if (status.length > 2 && !status[2].isEmpty()) {
                throw new RuntimeException("Loadosophia processing error: " + status[2]);
            }
            if (status[1].equals(STATUS_DONE)) {
                return Integer.parseInt(status[0]);
            }
        }
    }
}
