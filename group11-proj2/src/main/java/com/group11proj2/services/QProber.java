package com.group11proj2.services;

import com.group11proj2.util.CategoryHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QProber {
    private BingService bing;

    public QProber(BingService bing) {
        this.bing = bing;
    }

    public List<String> classify(Double specifityThreshold, Integer coverageThresahold, String host) throws Exception {
        return classifyHierarchical("Root", 1.0, specifityThreshold, coverageThresahold, host);
    }

    private List<String> classifyHierarchical(String node, double nodeSpecifity,
                                              Double specifityThreshold,
                                              Integer coverageThreshold, String host) throws Exception {
        List<String> result = new ArrayList<String>();
        List<String> candidateCat = CategoryHelper.getInstance().getSubCategories(node);
        int total_hits = 0;
        Map<String, Integer> hitCount = new HashMap<String, Integer>();
        for (String candidate : candidateCat) {
            hitCount.put(candidate, 0);
            List<String> probes = CategoryHelper.getInstance().getCategoryProbes(candidate);
            for (String probe : probes) {
                int hits = bing.query(host, probe);
                total_hits += hits;
                hitCount.put(candidate, hitCount.get(candidate) + hits);
            }
        }

        // Now find which child categories meet the coverage and specifity bars
        for (String candidate : candidateCat) {
            int candidate_hits = hitCount.get(candidate);
            double specifity = (candidate_hits * nodeSpecifity) / total_hits;
            if (candidate_hits >= coverageThreshold && specifity >= specifityThreshold) {
                // This works!
                // Now see if there are any subcategories that this host can be classified into
                List<String> matches = classifyHierarchical(candidate, specifity, specifityThreshold, coverageThreshold, host);
                result.addAll(matches);
            }
        }

        if (result.isEmpty()) {
            result.add(node);
        }
        return result;
    }

}
