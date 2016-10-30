package com.group11proj2.models;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProbeResult {
    List<String> classification;
    Map<String, Set<String>> documentSampleMap;

    public ProbeResult(List<String> classification, Map<String, Set<String>> documentSampleMap) {
        this.classification = classification;
        this.documentSampleMap = documentSampleMap;
    }

    public List<String> getClassification() {
        return classification;
    }

    public Set<String> getDocumentSample(String category) {
        if (documentSampleMap.containsKey(category)) {
            return documentSampleMap.get(category);
        }
        return new HashSet<String>();
    }
}
