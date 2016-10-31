package com.group11proj2;

import com.group11proj2.models.ProbeResult;
import com.group11proj2.services.BingService;
import com.group11proj2.services.QProber;

import java.io.*;
import java.security.CodeSource;
import java.util.*;

public class Proj2 {

    // if this is true, the program will read from the cache if available
    static boolean DEV_ENV = true;

    public static void main(String[] args) throws Exception {
        String accountKey = args[0];
        Double specifityThreshold = Double.parseDouble(args[1]);
        Integer coverageThreshold = Integer.parseInt(args[2]);
        String website = args[3];

        CodeSource codeSource = Proj2.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();
        File urlsFile = new File(jarDir + "/cache/" + website + "-urls-Root.txt");
        File hitsFile = new File(jarDir + "/cache/" + website + "-hits-Root.txt");

        System.out.println("Classifying...");
        QProber qb = new QProber(new BingService(accountKey), (DEV_ENV && urlsFile.exists()), (DEV_ENV && hitsFile.exists()));
        ProbeResult probe = qb.probe(specifityThreshold, coverageThreshold, website);

        List<String> classification = probe.getClassification();
        System.out.println();
        System.out.println("Classification:");
        String category = classification.get(classification.size() - 1);
        System.out.println(category);

        System.out.println();
        System.out.println("Extracting topic content summaries...");

        //TODO complete: construct content summary
//        Set<String> docSample = probe.getDocumentSample("Root/Health");

        return;
    }
}