package com.group11proj1.services;

import com.group11proj1.models.BingResult;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;

class CandidateWord implements Comparable {
    public String word;
    public double rdp = 0;
    public double idp = 0;
    public int rtf = 0;

    public CandidateWord(String w) {
        this.word = w;
    }

    public double getRelevanceScore() {
        return rdp - idp;
    }

    public int compareTo(Object other) {
        CandidateWord w = (CandidateWord) other;
        if (this.getRelevanceScore() == w.getRelevanceScore()) {
            return w.rtf - this.rtf;
        }
        return (int)(w.getRelevanceScore() - this.getRelevanceScore());
    }
}

public class QueryService {

    private static List<String> STOP_WORDS = null;
    private BingService bing;

    public QueryService(String accountKey) {
        bing = new BingService(accountKey);
    }

    public void search(String userQuery, Double precisionTarget, Writer writer) throws IOException {
        if (STOP_WORDS == null) {
            loadStopWords();
        }
        System.out.println("Parameters:");
        System.out.println("Key: " + bing.getKey());
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
        excluded.addAll(STOP_WORDS);

        List<String> queryWords = getAsWords(query);
        excluded.addAll(queryWords);

        for (BingResult r : irrelevant) {
            processBingResult(r, false, candidates, excluded);
        }

        for (BingResult r : relevant) {
            processBingResult(r, true, candidates, excluded);
        }

        // normalize relevant document count and irrelevant document count as percentages.
        for (CandidateWord cw : candidates.values()) {
            cw.rdp /= relevant.size();
            cw.rdp *= 100;

            cw.idp /= irrelevant.size();
            cw.idp *= 100;
        }

        List<CandidateWord> candidateList = new ArrayList<>();
        for (CandidateWord w : candidates.values()) {
            candidateList.add(w);
        }

        Collections.sort(candidateList);

        // TODO: determine the best order of the words.
        queryWords.add(candidateList.get(0).word);
        if (candidateList.get(0).compareTo(candidateList.get(1)) == 0) {
            queryWords.add(candidateList.get(1).word);
        }
        StringBuilder res = new StringBuilder();
        for (String s : queryWords) {
            res.append(s + " ");
        }
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

    private void processBingResult(BingResult r, boolean relevant, Map<String, CandidateWord> candidates,  Set<String> excluded) {
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
            if (relevant) {
                cw.rdp++;
                cw.rtf += documentWords.get(w);
            }
            else {
                cw.idp++;
            }
        }
    }

    private void loadStopWords() throws IOException {
        STOP_WORDS = new ArrayList<>();
        File file = new File("stopwords.txt");
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()) {
            STOP_WORDS.add(scanner.nextLine());
        }
    }

}
