package com.school.naivebayes.initialize;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class SetupEnvironment {

    private static final String REGEX_VALUE = "[^a-zA-Z ]";

    private int totalSize;

    private static String stopWordsRegex;

    private Set<String> vocabulary = new HashSet<>();

    private List<MappedRecord> insults = new LinkedList<>();

    private List<MappedRecord> normals = new LinkedList<>();

    private List<LabelValue> trainData = new ArrayList<>();

    private List<LabelValue> testData = new ArrayList<>();

    static {
        try {
            //Finding stop words for English.
            //Creating regex for stop words.
            File file = new File(SetupEnvironment.class.getClassLoader().getResource("stopwords.txt").getFile());
            List<String> stopwords = Files.readAllLines(file.toPath());
            stopWordsRegex = stopwords.stream().collect(Collectors.joining("|", "\\b(", ")\\b\\s?"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SetupEnvironment() {
    }

    public void setup(int size, Type type) throws IOException, CsvValidationException {
        //Opening train data
        File file = new File(SetupEnvironment.class.getClassLoader().getResource("train.csv").getFile());
        try (CSVReader csvReader = new CSVReader(new FileReader(file));) {
            String[] values = null;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                //Deleting stop words for reading comment.
                String result = values[2].toLowerCase().replaceAll(stopWordsRegex, "");
                String[] resultWords = result.replaceAll(REGEX_VALUE, "").toLowerCase().split("\\s+");

                //Changed commend adding to list by insulting type.
                if ("1".equals(values[0])) {
                    insults.add(new MappedRecord(result.replaceAll(REGEX_VALUE, "").toLowerCase()));
                } else {
                    normals.add(new MappedRecord(result.replaceAll(REGEX_VALUE, "").toLowerCase()));
                }
                //Adding commend word to vocabulary set.
                //Vocabulary a set bcs for prevent duplicate records.
                vocabulary.addAll(Arrays.asList(resultWords));
            }
            //Calculating total words size after reading csv file.
            totalSize = vocabulary.size();
        }
        initializeTestData(size, type);
    }

    private void initializeTestData(int size, Type type) {
        Random rand = new Random();

        //Calculating train and test data by wanted train size.
        int traineSize = size * 80 / 100;
        int testSize = size * 20 / 100;

        for (int i = 0; i < traineSize / (type.getRate() + 1); i++) {
            createData(type, rand, trainData);
        }

        for (int i = 0; i < testSize / (type.getRate() + 1); i++) {
            createData(type, rand, testData);
        }
    }

    private void createData(Type type, Random rand, List<LabelValue> dataList) {
        int insult;
        int normal;
        //Getting random data from insult list.
        insult = rand.nextInt(insults.size());
        dataList.add(new LabelValue(insults.get(insult), "1"));
        insults.remove(insult);

        //Getting random data from neutral data list.
        //This loop changes by type bcs we can apply different rates for neutral commends.
        for (int j = 0; j < type.getRate(); j++) {
            normal = rand.nextInt(normals.size());
            dataList.add(new LabelValue(normals.get(normal), "0"));
            normals.remove(normal);
        }
    }

    public ResultSet calculateAndShowResult() {
        // A means Actual Neutral, Predicted Neutral
        // B means Actual Neutral, Predicted Insult
        // C means Actual Insult, Predicted Neutral
        // D means Actual Insult, Predicted Insult
        double a = 0, b = 0, c = 0, d = 0;
        String selectClass = "0";
        for (int i = 0; i < testData.size(); i++) {
            //Resetting to min
            double bestCalc = Double.MIN_VALUE;
            for (int j = 0; j < trainData.size(); j++) {
                //Calculating for all set from traine data
                double calc = 1d / 2d;
                MappedRecord mappedRecord = trainData.get(j).getLabel();
                for (String word : testData.get(i).getLabel().getMappedWords().keySet()) {
                    //Multiplication for all words from commend
                    calc = calc * mappedRecord.getWordCalc(word, totalSize);
                }
                //Calculating class of commend
                if (calc > bestCalc) {
                    bestCalc = calc;
                    selectClass = trainData.get(j).getVal();
                }
            }

            //Increasing size of table.
            switch (selectClass + testData.get(i).getVal()) {
                case "11" -> d++;
                case "00" -> a++;
                case "10" -> b++;
                case "01" -> c++;
            }

        }
        ResultSet resultSet = new ResultSet();
        resultSet.setPrecision((a / (a + c)));
        resultSet.setRecall((a / (a + b)));
        resultSet.setF1((2 * a / (2 * a + b + c)));
        return resultSet;
    }

}
