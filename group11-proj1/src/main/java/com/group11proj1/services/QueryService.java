package com.group11proj1.services;

import com.group11proj1.models.BingResult;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

class CandidateWord implements Comparable<CandidateWord> {
    public String word;
    public int rdf = 0;
    public int tf = 0;

    public CandidateWord(String w) {
        this.word = w;
    }

    public int compareTo(CandidateWord w) {
        if (this.rdf == w.rdf) {
            return w.tf - this.tf;
        }
        return w.rdf - this.rdf;
    }
}

public class QueryService {

    // This list is from ranks.nl/stopwords
    private static final String[] STOP_WORDS = {"about", "an", "are", "as", "at", "be", "by", "com ", "for", "from",
            "how", "in", "is", "it", "of", "on", "or", "that", "the", "this", "to", "was", "what", "when", "where",
            "who", "will", "with", "www"
    };
    private BingService bing;

    public QueryService(String accountKey) {
        bing = new BingService(accountKey);
    }

    public void search(String userQuery, Double precisionTarget, Writer writer) throws IOException {
        System.out.println("Parameters:");
        System.out.println("Query: " + userQuery);
        System.out.println("Precision: " + precisionTarget);

        int round = 0;
        String query = userQuery;
        while (true) {
            round++;
            String separator = System.lineSeparator();
            writer.write("=====================================" + separator);
            writer.write("ROUND " + round + separator);
            writer.write("QUERY " + query + separator);

            List<BingResult> results = bing.query(query);
            List<BingResult> relevant = new ArrayList<>();
            List<BingResult> irrelevant = new ArrayList<>();
            for (BingResult result : results) {
                // Display result to user
                System.out.println("[");
                System.out.println("  URL: " + result.getUrl());
                System.out.println("  Title: " + result.getTitle());
                System.out.println("  Summary: " + result.getSummary());
                System.out.println("]");
                System.out.println("");

                // ask for user input, and classify result accordingly.
                Scanner reader = new Scanner(System.in);
                System.out.print("Relevant (Y/N)?");
                if (reader.next().toLowerCase().equals("y")) {
                    relevant.add(result);
                    writer.write("Relevant: YES " + separator);
                } else {
                    irrelevant.add(result);
                    writer.write("Relevant: NO " + separator);
                }
                writer.write("[" + separator);
                writer.write("  URL: " + result.getUrl() + separator);
                writer.write("  Title: " + result.getTitle() + separator);
                writer.write("  Summary: " + result.getSummary() + separator);
                writer.write("]" + separator);
                writer.write(separator);
            }

            if (relevant.size() == 0) {
                System.out.println("No relevant results found. Terminating");
                return;
            }

            if (results.size() < 10) {
                System.out.println("Total number of results less than 10. Terminating");
                return;
            }


            double precision = relevant.size() / 10.0;
            String precisionString = new DecimalFormat("#0.0").format(precision);
            writer.write("PRECISION: " + precisionString);

            System.out.println("======================");
            System.out.println("FEEDBACK SUMMARY");
            System.out.println("Query: " + query);
            System.out.println("Precision: " + precisionString);

            if (precision >= precisionTarget) {
                System.out.println("Desired precision reached, done");
                return;
            }

            query = modifyQuery(query, relevant, irrelevant);
        }
    }

    private String modifyQuery(String query, List<BingResult> relevant, List<BingResult> irrelevant) {
        Map<String, CandidateWord> candidates = new HashMap<>();
        Set<String> excluded = new HashSet<>();
        excluded.addAll(Arrays.asList(STOP_WORDS));

        List<String> queryWords = getAsWords(query);
        excluded.addAll(queryWords);

        for (BingResult r : irrelevant) {
            excluded.addAll(getAsWords(r.getTitle()));
            excluded.addAll(getAsWords(r.getSummary()));
        }

        for (BingResult r : relevant) {
            Map<String, Integer> documentWords = new HashMap<>();
            for (String s : getAsWords(r.getTitle())) {
                if (!excluded.contains(s)) {
                    int count = documentWords.containsKey(s) ? documentWords.get(s) : 0;
                    documentWords.put(s, ++count);
                }
            }

            for (String s : getAsWords(r.getSummary())) {
                if (!excluded.contains(s)) {
                    int count = documentWords.containsKey(s) ? documentWords.get(s) : 0;
                    documentWords.put(s, ++count);
                }
            }

            for (String w : documentWords.keySet()) {
                if (!candidates.containsKey(w)) {
                    candidates.put(w, new CandidateWord(w));
                }

                CandidateWord cw = candidates.get(w);
                cw.rdf++;
                cw.tf += documentWords.get(w);
            }
        }

        List<CandidateWord> sortedCandidates = candidates.values().stream().sorted().collect(Collectors.toList());

        // TODO: determine the best order of the words.
        queryWords.add(sortedCandidates.get(0).word);
        StringBuilder res = new StringBuilder();
        queryWords.forEach(s -> res.append(s + " "));
        res.deleteCharAt(res.length()-1);
        return res.toString();
    }

    private List<String> getAsWords(String text) {
        ArrayList<String> res = new ArrayList<>();
        String[] words = text.split("\\s+");
        // remove punctuation if it exists
        for (int i = 0; i < words.length; i++) {
            if (!Character.isAlphabetic(words[i].charAt(words[i].length()-1))) {
                words[i] = words[i].substring(0, words[i].length()-1);
            }

            if (words[i].length() <= 1)
                continue;
            res.add(words[i].toLowerCase());
        }
        return res;
    }

}
