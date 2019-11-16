package com.school.naivebayes;

import com.opencsv.exceptions.CsvValidationException;
import com.school.naivebayes.initialize.ResultSet;
import com.school.naivebayes.initialize.SetupEnvironment;
import com.school.naivebayes.initialize.Type;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            SetupEnvironment setupEnvironment = new SetupEnvironment();
            setupEnvironment.setup(1250, Type.ONE_TWO);
            ResultSet resultSet = setupEnvironment.calculateAndShowResult();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
