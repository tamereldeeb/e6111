package com.group11proj2.models;


import java.util.List;

public class BingServiceResult {
    Integer total;
    List<BingResult> top;

    public BingServiceResult(Integer total, List<BingResult> top) {
        this.total = total;
        this.top = top;
    }

    public Integer getTotalResults() {
        return total;
    }

    public List<BingResult> getTopResults() {
        return top;
    }
}
