package com.group11proj2;

import com.group11proj2.models.ProbeResult;
import com.group11proj2.services.BingService;
import com.group11proj2.services.QProber;
import com.group11proj2.util.CategoryHelper;
import com.group11proj2.util.WebHelper;

import java.util.*;

public class Proj2 {

    // if this is true, the program will read from the cache if available
    public static boolean DEV_ENV = true;

    public static void main(String[] args) throws Exception {
        String accountKey = args[0];
        Double specifityThreshold = Double.parseDouble(args[1]);
        Integer coverageThreshold = Integer.parseInt(args[2]);
        String website = args[3];

//        CodeSource codeSource = Proj2.class.getProtectionDomain().getCodeSource();
//        File jarFile = new File(codeSource.getLocation().toURI().getPath());
//        String cachePath = jarFile.getParentFile().getPath() + "/cache";
        String cachePath = "cache";

        System.out.println("Classifying...");
        QProber qb = new QProber(new BingService(accountKey), cachePath);
        ProbeResult probe = qb.probe(specifityThreshold, coverageThreshold, website);

        List<String> classification = probe.getClassification();
        System.out.println();
        System.out.println("Classification:");
        String category = classification.get(classification.size() - 1);
        System.out.println(category);

        System.out.println();
        System.out.println("Extracting topic content summaries...");
        Set<String> docSample;
        do {
            docSample = probe.getDocumentSample(category);
            if (docSample.size() > 0) {
                String[] nodes = category.split("/");
                String leaf = nodes[nodes.length-1];
                System.out.println("Creating Content Summary for:" + leaf);
                System.out.println();
                WebHelper.getInstance().constructContentSummary(leaf + "-" + website +  ".txt", docSample, cachePath);
            }
            category = CategoryHelper.getParent(category);
        } while(!category.equals(""));

        return;
    }
}