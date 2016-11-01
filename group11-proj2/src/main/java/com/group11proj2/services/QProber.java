package com.group11proj2.services;

import com.group11proj2.Proj2;
import com.group11proj2.models.BingResult;
import com.group11proj2.models.BingServiceResult;
import com.group11proj2.models.ProbeResult;
import com.group11proj2.util.CategoryHelper;

import java.io.*;
import java.util.*;

public class QProber {
    private BingService bing;
    private String cachePath;
    private Boolean hitsCached;
    private Boolean urlsCached;

    public QProber(BingService bing, String cachePath) {
        this.bing = bing;
        this.cachePath = cachePath;
        this.urlsCached = false;
        this.hitsCached = false;
    }

    public ProbeResult probe(Double specificityThreshold, Integer coverageThresahold, String host) throws Exception {
        File cacheDir = new File(cachePath);
        File urlsFile = new File(cachePath + "/" + host + "-urls-Root.txt");
        File hitsFile = new File(cachePath + "/" + host + "-hits-Root.txt");
        this.urlsCached = Proj2.DEV_ENV && urlsFile.exists();
        this.hitsCached = Proj2.DEV_ENV && hitsFile.exists();

        Map<String, Set<String>> documentSampleMap = new HashMap<>();
        List<String> classification = classifyHierarchical("Root", 1.0, specificityThreshold, coverageThresahold, host, documentSampleMap, cacheDir);

        //cache logic
        if (urlsCached) {
            // initialize documentSample from cache
            File[] cacheList = cacheDir.listFiles();
            for (File f : cacheList) {
                if (f.isFile()) {
                    String[] parts = f.getName().split("-", 3);
                    if (parts[0].equals(host) && parts[1].equals("urls")) {
                        String tempCat = parts[2].replaceAll("-", "/").replace(".txt", "");
                        documentSampleMap.put(tempCat, new HashSet<String>());
                        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                documentSampleMap.get(tempCat).add(line);
                            }
                        }
                    }
                }
            }
        } else {
            // write cache
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            String category = classification.get(classification.size() - 1);
            File urlFile;
            BufferedWriter writer = null;
            do {
                urlFile = new File(cachePath+"/"+host+"-urls-"+ category.replaceAll("/", "-") +".txt");
                if (!urlFile.exists()) {
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(urlFile), "utf-8"));
                        Set<String> docSample = documentSampleMap.get(category);
                        for (String url : docSample) {
                            writer.write(url);
                            writer.newLine();
                        }
                    } catch (Exception e) {
                        // ignore
//                        System.out.println("Urls cache writing failed: " + e.toString());
                    } finally {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                }
                category = CategoryHelper.getParent(category);
            } while(!category.equals(""));
        }

        return new ProbeResult(classification, documentSampleMap);
    }

    private List<String> classifyHierarchical(String node, double nodeSpecificity,
                                              Double specificityThreshold,
                                              Integer coverageThreshold, String host,
                                              Map<String, Set<String>> documentSampleMap,
                                              File cacheDir) throws Exception {
        List<String> result = new ArrayList<>();
        List<String> candidateCat = CategoryHelper.getInstance().getSubCategories(node);
        Integer total_hits = 0;
        Map<String, Integer> hitCount = new HashMap<>();
        for (String candidate : candidateCat) {
            hitCount.put(candidate, 0);
            List<String> probes = CategoryHelper.getInstance().getCategoryProbes(candidate);
            for (String probe : probes) {
                BingServiceResult bingResult = bing.query(host, probe, urlsCached, hitsCached);
                if (bingResult.getTopResults() != null) {
                    addToDocumentSample(node, documentSampleMap, bingResult.getTopResults());
                }
                if (bingResult.getTotalResults() != null) {
                    Integer hits = bingResult.getTotalResults();
                    total_hits += hits;
                    hitCount.put(candidate, hitCount.get(candidate) + hits);
                }
            }
        }

        File hitsFile = new File(cacheDir+"/"+host+"-hits-"+ node.replaceAll("/", "-") +".txt");
        if (hitsCached) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(hitsFile));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":", 3);
                    hitCount.put(parts[0], Integer.parseInt(parts[1]));
                    total_hits = Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                //
            } finally {
                if (br != null) { br.close(); }
            }
        } else {
            // write cache
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            if (!hitsFile.exists()) {
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hitsFile), "utf-8"));
                    Set<String> keys = hitCount.keySet();
                    for (String key : keys) {
                        writer.write(key + ":" + hitCount.get(key) + ":" + total_hits);
                        writer.newLine();
                    }
                } catch (Exception e) {
                    System.out.println("Hits cache writing failed: " + e.toString());
                } finally {
                    if (writer!= null) { writer.close(); }
                }
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
                List<String> matches = classifyHierarchical(candidate, specificity, specificityThreshold, coverageThreshold, host, documentSampleMap, cacheDir);
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
