package com.group11proj2.models;


import java.util.List;

public class BingServiceResult {
    int total;
    List<BingResult> top;

    public BingServiceResult(int total, List<BingResult> top) {
        this.total = total;
        this.top = top;
    }

    public int getTotalResults() {
        return total;
    }

    public List<BingResult> getTopResults() {
        return top;
    }
}
