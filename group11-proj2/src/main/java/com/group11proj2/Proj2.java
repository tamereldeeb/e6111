package com.group11proj2;

import com.group11proj2.models.ProbeResult;
import com.group11proj2.services.BingService;
import com.group11proj2.services.QProber;

import java.util.List;
import java.util.Set;

public class Proj2 {

    public static void main(String[] args) throws Exception {
        String accountKey = args[0];
        Double specifityThreshold = Double.parseDouble(args[1]);
        Integer coverageThreshold = Integer.parseInt(args[2]);
        String website = args[3];

        System.out.println("Classifying...");
        QProber qb = new QProber(new BingService(accountKey));
        ProbeResult probe = qb.probe(specifityThreshold, coverageThreshold, website);
        List<String> classification = probe.getClassification();
        Set<String> docSample = probe.getDocumentSample("Root/Health");
        System.out.println();
        System.out.println("Classification:");
        for (String category : classification) {
            System.out.println(category);
        }
        return;
    }
}