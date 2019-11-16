package com.school.naivebayes.initialize;

import java.util.HashMap;
import java.util.Map;

public class MappedRecord {
    private String text;
    private int total;
    private Map<String, Integer> mappedWords = new HashMap<>();

    public MappedRecord(String text) {
        // Burada ilgili text için maplemeler yapılıyor ve toplam kelime sayısı hesaplanıyor.
        this.text = text;
        String[] words = text.split(" ");
        for (String word : words) {
            if (mappedWords.containsKey(word)) {
                mappedWords.put(word, mappedWords.get(word) + 1);
            } else {
                mappedWords.put(word, 1);
            }
        }
        total = words.length;
    }

    public String getText() {
        return text;
    }

    public Map<String, Integer> getMappedWords() {
        return mappedWords;
    }

    public double getWordCalc(String word, int totalSize) {
        //Verilen kelime için, ilgili sınıfta bayes teoremine göre olasılık hesbaı yapılıyor.
        if (!mappedWords.containsKey(word)) {
            return 1d / ((double)total + (double)totalSize);
        }
        return ((double)mappedWords.get(word) + 1d) / ((double) total + (double)totalSize);
    }
}
