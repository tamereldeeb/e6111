package com.group11proj3;

import java.util.ArrayList;
import java.util.List;

public class AssociationRule {
    List<String> lhs;
    List<String> rhs;
    double support;
    double confidence;

    public AssociationRule(List<String> lhs, List<String> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        support = 0;
        confidence = 0;
    }

    public List<String> getLhs() {
        return lhs;
    }

    public List<String> getRhs() {
        return rhs;
    }

    public double getSupport() {
        return support;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setSupport(double support) {
        this.support = support;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

}
