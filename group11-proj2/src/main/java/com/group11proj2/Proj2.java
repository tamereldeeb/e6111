package com.group11proj2;

import com.group11proj2.services.BingService;

public class Proj2 {

    public static void main(String[] args) throws Exception {
        String accountKey = args[0];
        Double specifityThreshold = Double.parseDouble(args[1]);
        Integer coverageThresahold = Integer.parseInt(args[2]);
        String website = args[3];

        BingService bing = new BingService("4LqedL4leJhU1WKa1lSuKxpbEGlvWxFZjXYc++Xk4sk");
        bing.query(website, "Health diabetes");
    }
}