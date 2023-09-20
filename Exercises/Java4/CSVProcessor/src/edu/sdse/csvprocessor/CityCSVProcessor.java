package edu.sdse.csvprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

record CityRecord(int id, int year, String city, int population) {
    public String toString() {
        return "id: " + id +
                ", year: " + year +
                ", city: " + city +
                ", population: " + population;
    }
}

public class CityCSVProcessor {

    public void readAndProcess(File file) {
        //Try with resource statement (as of Java 8)
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            //Discard header row
            br.readLine();

            String line;

            ArrayList<CityRecord> allRecords = new ArrayList<>();
            HashMap<String, ArrayList<CityRecord>> recordsByCity = new HashMap<>();

            while ((line = br.readLine()) != null) {
                // Parse each line
                String[] rawValues = line.split(",");

                int id = convertToInt(rawValues[0]);
                int year = convertToInt(rawValues[1]);
                String city = convertToString(rawValues[2]);
                int population = convertToInt(rawValues[3]);

                CityRecord record = new CityRecord(id, year, city, population);

                allRecords.add(record);
                if (!recordsByCity.containsKey(city)) {
                    recordsByCity.put(city, new ArrayList<>());
                }
                recordsByCity.get(city).add(record);
            }
            for (Entry<String, ArrayList<CityRecord>> entry : recordsByCity.entrySet()) {
                String city = entry.getKey();
                ArrayList<CityRecord> recordsOfCity = entry.getValue();
                int totalPopulation = 0;
                int minYear = 100000;
                int maxYear = 0;
                for (CityRecord record : recordsOfCity) {
                    totalPopulation += record.population();
                    if (record.year() < minYear) {
                        minYear = record.year();
                    }
                    if (record.year() > maxYear) {
                        maxYear = record.year();
                    }
                }
                int averagePopulation = totalPopulation / recordsOfCity.size();
                System.out.println("Average population of " + city + " (" + minYear + "-" + maxYear + "): " + averagePopulation);
            }
        } catch (Exception e) {
            System.err.println("An error occurred:");
            e.printStackTrace();
        }
    }

    private String cleanRawValue(String rawValue) {
        return rawValue.trim();
    }

    private int convertToInt(String rawValue) {
        rawValue = cleanRawValue(rawValue);
        return Integer.parseInt(rawValue);
    }

    private String convertToString(String rawValue) {
        rawValue = cleanRawValue(rawValue);

        if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
            return rawValue.substring(1, rawValue.length() - 1);
        }

        return rawValue;
    }

    public static final void main(String[] args) {
        CityCSVProcessor reader = new CityCSVProcessor();

        File dataDirectory = new File("data/");
        File csvFile = new File(dataDirectory, "Cities.csv");

        reader.readAndProcess(csvFile);
    }
}
