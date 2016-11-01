package com.group11proj2.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class WebHelper {

    private static WebHelper instance = null;

    public static WebHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new WebHelper();
            instance.init();
        }
        return instance;
    }

    private void init() {
        //
    }

    public void constructContentSummary(String fileName, Set<String> docSample, String cachePath) throws Exception {

        Map<String, Integer> docFreqCount = new TreeMap<>();

        for (String url: docSample) {
            processURLContents(cachePath, url, docFreqCount);
        }

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), "utf-8"));

            for (Map.Entry<String,Integer> entry : docFreqCount.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                writer.write(key + "#" + String.format("%d.0", value));
                writer.write(System.lineSeparator());
            }
        } catch (IOException ex) {
            // ignore
//            ex.printStackTrace();
        } finally {
            if (writer != null) { writer.close(); }
        }
    }

    public void processURLContents(String cachePath, String url, Map<String, Integer> docFreqCount) {
        if (!url.contains("https")) {
            String temp = url.replace("http://", "");
            if (!temp.contains(".html")) {
                temp += ".html";
            }
            String filePath = cachePath + "/" + temp;
            File urlFile = new File(filePath);

            // get URL page
            try {
                if (!urlFile.exists()) {
                    System.out.println("Getting page: " + url);
                    System.out.println();
                    FileUtils.copyURLToFile(new URL(url), urlFile);
                } else {
                    System.out.println("Getting page: " + url + "(cached)");
                    System.out.println();
                }
            } catch (Exception e) {
                // ignore
//            System.out.println("URL download failed: " + e.toString());
            }

            // process file
            Set words = getWordsLynx.runLynx(filePath);
            for (Object w : words) {
                String token = (String) w;
                if (docFreqCount.containsKey(w)) {
                    docFreqCount.put(token, docFreqCount.get(token) + 1);
                } else {
                    docFreqCount.put(token, 1);
                }
            }
        }
    }
}
