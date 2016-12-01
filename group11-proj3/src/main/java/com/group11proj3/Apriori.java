package com.group11proj3;

import java.util.*;

public class Apriori {

    public static Map<ItemSet, Double> computeFrequentItemSets(List<List<String>> marketBaskets, double minSupport) {
        Map<ItemSet, Double> result = new TreeMap<ItemSet, Double>();

        ArrayList<List<ItemSet>> L = new ArrayList<List<ItemSet>>();
        L.add(new ArrayList<ItemSet>());
        L.get(0).add(new ItemSet(new ArrayList<String>()));

        // Compute frequent itemsets of size 1
        List<ItemSet> L1 = new ArrayList<ItemSet>();
        L.add(L1);
        Map<String, Integer> count = new HashMap<String, Integer>();
        for (int i = 0; i < marketBaskets.size(); i++) {
            List<String> basket = marketBaskets.get(i);
            for (String s : basket) {
                if (!count.containsKey(s)) {
                    count.put(s, 0);
                }
                count.put(s, count.get(s) + 1);
            }
        }

        for (String s : count.keySet()) {
            double support = (1.0 * count.get(s)) / marketBaskets.size();
            if (support >= minSupport) {
                List<String> list = new ArrayList<String>();
                list.add(s);
                ItemSet is = new ItemSet(list);
                is.setSupport(support);
                L1.add(is);
            }
        }

        // Apply apriori algorithm to compute frequent item sets larger than 1
        int i = 1;
        while (L.get(i).size() > 0) {
            List<ItemSet> Lp = L.get(i);

            List<ItemSet> Li = new ArrayList<ItemSet>();
            L.add(Li);
            i++;

            Map<ItemSet, Integer> candidates = new TreeMap<ItemSet, Integer>();
            for (int x = 0; x < Lp.size(); x++) {
                for (int y = x-1; y >= 0; y--) {
                    if (Lp.get(x).matchingPrefix(Lp.get(y))) {
                        ItemSet s = Lp.get(x).join(Lp.get(y));
                        candidates.put(s, 0);
                    } else {
                        break;
                    }
                }
            }

            for (List<String> mb : marketBaskets) {
                updateSupported(mb, i, candidates);
            }

            for (ItemSet s : candidates.keySet()) {
                double support = (1.0 * candidates.get(s)) / marketBaskets.size();
                if (support >= minSupport) {
                    s.setSupport(support);
                    Li.add(s);
                }
            }

        }

        for (int x = 1; x < L.size(); x++) {
            for (ItemSet s : L.get(x)) {
                result.put(s, s.getSupport());
            }
        }
        return result;
    }

    private static void updateSupported(List<String> marketBasket, int length, Map<ItemSet, Integer> candidates) {
        for (int mask = 0; mask < (1<<marketBasket.size()); mask++) {
            List<String> subset = new ArrayList<String>();
            for (int i = 0; i < marketBasket.size(); i++) {
                if ((mask & (1<<i)) > 0) {
                    subset.add(marketBasket.get(i));
                }
            }
            if (subset.size() == length) {
                ItemSet s = new ItemSet(subset);
                if (candidates.containsKey(s)) {
                    candidates.put(s, candidates.get(s) + 1);
                }
            }
        }
    }
}
