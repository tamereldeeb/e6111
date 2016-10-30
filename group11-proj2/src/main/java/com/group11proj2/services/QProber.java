package com.group11proj2.services;

import com.group11proj2.models.BingResult;
import com.group11proj2.models.BingServiceResult;
import com.group11proj2.models.ProbeResult;
import com.group11proj2.util.CategoryHelper;

import java.io.IOException;
import java.util.*;

public class QProber {
    private BingService bing;

    public QProber(BingService bing) {
        this.bing = bing;
    }

    public ProbeResult probe(Double specificityThreshold, Integer coverageThresahold, String host) throws Exception {
        Map<String, Set<String>> documentSampleMap = new HashMap<String, Set<String>>();
        List<String> classification = classifyHierarchical("Root", 1.0, specificityThreshold, coverageThresahold, host, documentSampleMap);
        return new ProbeResult(classification, documentSampleMap);
    }

    private List<String> classifyHierarchical(String node, double nodeSpecificity,
                                              Double specificityThreshold,
                                              Integer coverageThreshold, String host,
                                              Map<String, Set<String>> documentSampleMap) throws Exception {
        List<String> result = new ArrayList<String>();
        List<String> candidateCat = CategoryHelper.getInstance().getSubCategories(node);
        int total_hits = 0;
        Map<String, Integer> hitCount = new HashMap<String, Integer>();
        for (String candidate : candidateCat) {
            hitCount.put(candidate, 0);
            List<String> probes = CategoryHelper.getInstance().getCategoryProbes(candidate);
            for (String probe : probes) {
                BingServiceResult bingResult = bing.query(host, probe);
                addToDocumentSample(node, documentSampleMap, bingResult.getTopResults());
                int hits = bingResult.getTotalResults();
                total_hits += hits;
                hitCount.put(candidate, hitCount.get(candidate) + hits);
            }
        }

        // Now find which child categories meet the coverage and specificity bars
        for (String candidate : candidateCat) {
            int coverage = hitCount.get(candidate);
            double specificity = total_hits > 0 ? (coverage * nodeSpecificity) / total_hits : 1;
            printCategoryResult(candidate, coverage, specificity);
            if (coverage >= coverageThreshold && specificity >= specificityThreshold) {
                // This works!
                // Now see if there are any subcategories that this host can be classified into
                List<String> matches = classifyHierarchical(candidate, specificity, specificityThreshold, coverageThreshold, host, documentSampleMap);
                result.addAll(matches);
            }
        }

        if (result.isEmpty()) {
            result.add(node);
        }
        return result;
    }

    private void printCategoryResult(String category, int coverage, double specificity) {
        String[] nodes = category.split("/");
        String leaf = nodes[nodes.length-1];
        System.out.println("Specificity for category:" + leaf + " is " + specificity);
        System.out.println("Coverage for category:" + leaf + " is " + coverage);
    }

    private void addToDocumentSample(String category, Map<String, Set<String>> documentSampleMap, List<BingResult> results) {
        for (BingResult r : results) {
            String node = category;
            while (!node.equals("")) {
                if (!documentSampleMap.containsKey(node)) {
                    documentSampleMap.put(node, new HashSet<String>());
                }
                documentSampleMap.get(node).add(r.getUrl());
                node = CategoryHelper.getParent(node);
            }
        }
    }

}
