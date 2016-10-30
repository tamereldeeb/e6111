package com.group11proj2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CategoryHelper {

    private static CategoryHelper instance = null;

    private Map<String, List<String>> subcategories = new HashMap<>();
    private Map<String, List<String>> probes = new HashMap<>();

    public static CategoryHelper getInstance() throws Exception {
        if (instance == null) {
            instance = new CategoryHelper();
            instance.init();
        }
        return instance;
    }

    private void init() throws IOException {
        String[] root = {"Root/Computers", "Root/Health", "Root/Sports"};
        subcategories.put("Root", Arrays.asList(root));

        String[] computers = {"Root/Computers/Hardware", "Root/Computers/Programming"};
        subcategories.put("Root/Computers", Arrays.asList(computers));

        String[] health = {"Root/Health/Fitness", "Root/Health/Diseases"};
        subcategories.put("Root/Health", Arrays.asList(health));

        String[] sports = {"Root/Sports/Basketball", "Root/Sports/Soccer"};
        subcategories.put("Root/Sports", Arrays.asList(sports));

        readProbes("root.txt", "Root");
        readProbes("computers.txt", "Root/Computers");
        readProbes("health.txt", "Root/Health");
        readProbes("sports.txt", "Root/Sports");
    }

    public List<String> getSubCategories(String category) {
        List<String> ret = subcategories.get(category);
        if (ret == null)
        {
           ret = new ArrayList<String>();
        }
        return ret;
    }

    public List<String> getCategoryProbes(String category) {
        List<String> ret = probes.get(category);
        if (ret == null)
        {
            ret = new ArrayList<String>();
        }
        return ret;
    }

    private void readProbes(String filename, String category) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("probes/" + filename);
        Scanner scanner = new Scanner(input);
        while(scanner.hasNextLine()) {
            String childCategory = category + "/" + scanner.next();
            String probe = scanner.nextLine();

            if (probe.startsWith(" ")) {
                probe = probe.substring(1);
            }

            if (probe.endsWith(" ")) {
                probe = probe.substring(0, probe.length()-1);
            }

            if (!probes.containsKey(childCategory)) {
                probes.put(childCategory, new ArrayList<String>());
            }

            probes.get(childCategory).add(probe);
        }
    }
}
