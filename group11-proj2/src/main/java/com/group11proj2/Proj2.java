package com.group11proj2;

import com.group11proj2.services.BingService;
import com.group11proj2.services.QProber;

import java.util.List;

public class Proj2 {

    public static void main(String[] args) throws Exception {
        String accountKey = args[0];
        Double specifityThreshold = Double.parseDouble(args[1]);
        Integer coverageThreshold = Integer.parseInt(args[2]);
        String website = args[3];

        QProber qb = new QProber(new BingService(accountKey));
        List<String> cats = qb.classify(specifityThreshold, coverageThreshold, website);
        return;
    }
}