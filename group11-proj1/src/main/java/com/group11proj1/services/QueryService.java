package com.group11proj1.services;

import com.group11proj1.models.BingResult;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryService {

    private BingService bing;

    public QueryService(String accountKey) {
        bing = new BingService(accountKey);
    }

    public void search(String userQuery, Double precisionTarget, Writer writer) throws IOException {
        System.out.println("Parameters:");
        System.out.println("Query: " + userQuery);
        System.out.println("Precision: " + precisionTarget);

        int round = 0;
        String query = userQuery;
        while (true) {
            round++;
            String separator = System.lineSeparator();
            writer.write("=====================================" + separator);
            writer.write("ROUND " + round + separator);
            writer.write("QUERY " + query + separator);

            List<BingResult> results = bing.query(query);
            List<BingResult> relevant = new ArrayList<>();
            List<BingResult> irrelevant = new ArrayList<>();
            for (BingResult result : results) {
                // Display result to user
                System.out.println("[");
                System.out.println("  URL: " + result.getUrl());
                System.out.println("  Title: " + result.getTitle());
                System.out.println("  Summary: " + result.getSummary());
                System.out.println("]");
                System.out.println("");

                // ask for user input, and classify result accordingly.
                Scanner reader = new Scanner(System.in);
                System.out.print("Relevant (Y/N)?");
                if (reader.next().toLowerCase().equals("y")) {
                    relevant.add(result);
                    writer.write("Relevant: YES " + separator);
                } else {
                    irrelevant.add(result);
                    writer.write("Relevant: NO " + separator);
                }
                writer.write("[" + separator);
                writer.write("  URL: " + result.getUrl() + separator);
                writer.write("  Title: " + result.getTitle() + separator);
                writer.write("  Summary: " + result.getSummary() + separator);
                writer.write("]" + separator);
                writer.write(separator);
            }

            if (relevant.size() == 0) {
                System.out.println("No relevant results found. Terminating");
                return;
            }

            if (results.size() < 10) {
                System.out.println("Total number of results less than 10. Terminating");
                return;
            }


            double precision = relevant.size() / 10.0;
            String precisionString = new DecimalFormat("#0.0").format(precision);
            writer.write("PRECISION: " + precisionString);

            System.out.println("======================");
            System.out.println("FEEDBACK SUMMARY");
            System.out.println("Query: " + query);
            System.out.println("Precision: " + precisionString);

            if (precision >= precisionTarget) {
                System.out.println("Desired precision reached, done");
                return;
            }

            // TODO: augment query.
            System.out.println("Query should be augmented here to improve precision");
            return;
        }
    }
}
