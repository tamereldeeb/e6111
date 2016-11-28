package com.group11proj3;

import java.util.*;

public class Proj3 {

    public static void main(String[] args) throws Exception {
        List<List<String>> baskets = new ArrayList<List<String>>();

        List<String> l1 = new ArrayList<String>();
        l1.add("pen"); l1.add("ink"); l1.add("diary"); l1.add("soap");
        Collections.sort(l1);
        baskets.add(l1);

        List<String> l2 = new ArrayList<String>();
        l2.add("pen"); l2.add("ink"); l2.add("diary");
        Collections.sort(l2);
        baskets.add(l2);

        List<String> l3 = new ArrayList<String>();
        l3.add("pen"); l3.add("diary");
        Collections.sort(l3);
        baskets.add(l3);

        List<String> l4 = new ArrayList<String>();
        l4.add("pen"); l4.add("ink"); l4.add("soap");
        Collections.sort(l4);
        baskets.add(l4);

        Apriori.computeFrequentItemSets(baskets, 0.7);

    }
}