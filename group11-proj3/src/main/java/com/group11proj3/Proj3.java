package com.group11proj3;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

public class Proj3 {

    public static void main(String[] args) throws Exception {
        double supportThreshold = 0.7;
        double confidenceThreshold = 0.8;
        String integratedDatasetFile = "INTEGRATED-DATASET.csv";

        List<List<String>> baskets = readDataset(integratedDatasetFile);

        // Compute frequent item sets using apriori
        Map<ItemSet, Double> fis = Apriori.computeFrequentItemSets(baskets, supportThreshold);

        List<AssociationRule> rules = new ArrayList<AssociationRule>();

        // Now compute association rules meeting support threshold
        for (ItemSet s : fis.keySet()) {
            List<String> items = s.getItems();
            for (int mask = 1; mask < (1<<items.size())-1; mask++) {
                List<String> lhs = new ArrayList<String>();
                List<String> rhs = new ArrayList<String>();
                for (int i = 0; i < items.size(); i++) {
                    if ((mask & (1<<i)) > 0) {
                        lhs.add(items.get(i));
                    } else {
                        rhs.add(items.get(i));
                    }
                }

                ItemSet left = new ItemSet(lhs);
                double support = s.getSupport();
                double lhsSupport = fis.get(left);
                double confidence = support / lhsSupport;
                if (confidence >= confidenceThreshold) {
                    AssociationRule rule = new AssociationRule(lhs, rhs);
                    rule.setSupport(support);
                    rule.setConfidence(confidence);
                    rules.add(rule);
                }
            }
        }

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt", false), "utf-8"));
        printFrequentItemSets(fis, supportThreshold, writer);
        writer.write(System.lineSeparator());
        printAssociationRules(rules, confidenceThreshold, writer);
        writer.flush();
        System.out.println();
    }

    private static List<List<String>> readDataset(String csvPath) throws IOException {
        List<List<String>> results = new ArrayList<List<String>>();
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        for (CSVRecord record : records) {
            List<String> basket = new ArrayList<String>();
            for (int i = 0; i < record.size(); i++) {
                basket.add(record.get(i));
            }
            results.add(basket);
        }
        return results;
    }

    private static void printFrequentItemSets(Map<ItemSet, Double> fis, Double minSupport, Writer writer) throws IOException {
        String separator = System.lineSeparator();
        writer.write("==Frequent itemsets (min_sup="+(minSupport*100)+"%)");
        writer.write(separator);

        List<ItemSet> sorted = new ArrayList<ItemSet>();
        for (ItemSet s : fis.keySet()) {
            sorted.add(s);
        }
        Collections.sort(sorted, new Comparator<ItemSet>() {
            @Override
            public int compare(ItemSet a, ItemSet b) {
                if (a.getSupport() < b.getSupport())
                    return 1;
                if (a.getSupport() == b.getSupport())
                    return 0;
                return -1;
            }
        });

        for (ItemSet s : sorted) {
            writer.write("[");
            for (int i = 0; i < s.getItems().size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }
                writer.write(s.getItems().get(i));
            }
            writer.write("], ");
            writer.write((s.getSupport() * 100) + "%");
            writer.write(separator);
        }
    }

    private static void printAssociationRules(List<AssociationRule> rules, Double minConfidence, Writer writer) throws IOException {
        String separator = System.lineSeparator();
        writer.write("==High-confidence association rules (min_conf="+(minConfidence*100)+"%)");
        writer.write(separator);

        Collections.sort(rules, new Comparator<AssociationRule>() {
            @Override
            public int compare(AssociationRule a, AssociationRule b) {
                if (a.getConfidence() < b.getConfidence())
                    return 1;
                if (a.getConfidence() == b.getConfidence())
                    return 0;
                return -1;
            }
        });

        for (AssociationRule r : rules) {
            writer.write("[");
            for (int i = 0; i < r.getLhs().size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }
                writer.write(r.getLhs().get(i));
            }
            writer.write("]=>[");
            for (int i = 0; i < r.getRhs().size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }
                writer.write(r.getRhs().get(i));
            }
            writer.write("]");
            writer.write("(Conf: ");
            writer.write((r.getConfidence() * 100) + "%");
            writer.write(", Supp: ");
            writer.write((r.getSupport() * 100) + "%");
            writer.write(")");
            writer.write(separator);
        }
    }
}