package com.group11proj3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemSet implements Comparable {
    List<String> items = new ArrayList<String>();
    double support = 0;

    public ItemSet(List<String> i) {
        items = i;
        Collections.sort(items);
    }

    public List<String> getItems() {
        return items;
    }

    public double getSupport() {
        return support;
    }

    public void setSupport(double s) {
        support = s;
    }

    public int compareTo(Object other) {
        ItemSet o = (ItemSet) other;
        if (items.size() == o.items.size()) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).compareTo(o.items.get(i)) != 0) {
                    return items.get(i).compareTo(o.items.get(i));
                }
            }
            return 0;
        }
        return items.size() - o.items.size();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other instanceof ItemSet) {
            return compareTo(other) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    public boolean matchingPrefix(ItemSet other) {
        if (items.size() == other.items.size()) {
            for (int i = 0; i < items.size()-1; i++) {
                if (!items.get(i).equals(other.items.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public ItemSet join(ItemSet other) {
        ArrayList<String> items = new ArrayList<String>(this.items);
        items.add(other.items.get(items.size()-1));
        return new ItemSet(items);
    }

}
