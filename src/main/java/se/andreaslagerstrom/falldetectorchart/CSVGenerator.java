package se.andreaslagerstrom.falldetectorchart;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CSVGenerator {

    public static void generateCsv(List<Fall> falls, File directory) {
        System.out.println("Generating falls");
        Date saveDate = new Date();
        List<Fall> iOSFalls = falls.stream().filter(fall -> fall.getDevice().contains("iPhone")).collect(Collectors.toList());
        List<Fall> androidFalls = falls.stream().filter(fall -> !fall.getDevice().contains("iPhone")).collect(Collectors.toList());
        printToFile(falls, directory.getAbsolutePath() + "/Falls_data_" + saveDate.toString());
        printToFile(iOSFalls, directory.getAbsolutePath() + "/Falls_data_" + saveDate.toString() + "_iOS_only");
        printToFile(androidFalls, directory + "/Falls_data_" + saveDate + "_Android_only");
    }

    private static void printToFile(List<Fall> falls, String fileName) {

        String[] headers = {"Impact_Start", "Impact_End", "Average_Acceleration", "Impact_Duration",
                "Impact_Peak_Value", "Impact_Peak_Duration", "Longest_Valley_Value",
                "Longest_Valley_Duration", "Num_Peaks_Prior_To_Impact", "Num_Valleys_Prior_To_Impact", "Class", "Class_Number"};

        try (BufferedWriter reader = Files.newBufferedWriter(Paths.get(fileName + ".csv"));
             CSVPrinter printer = new CSVPrinter(reader, CSVFormat.DEFAULT.withHeader(headers))) {

            for (Fall fall : falls) {
                if (isValidFall(fall))
                    printer.printRecord(
                            fall.getImpactStart(),
                            fall.getImpactEnd(),
                            fall.getAverageAcceleration(),
                            fall.getImpactDuration(),
                            fall.getImpactPeakValue(),
                            fall.getImpactPeakDuration(),
                            fall.getLongestValleyValue(),
                            fall.getLongestValleyDuration(),
                            fall.getNumberOfPeaksPriorToImpact(),
                            fall.getNumberOfValleysPriorToImpact(),
                            fall.getClassificationType().name(),
                            fall.getClassificationType().getNumValue());
            }

            printer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidFall(Fall fall) {
        return fall.getImpactStart() != null &&
                fall.getImpactEnd() != null &&
                fall.getAverageAcceleration() != null &&
                fall.getImpactDuration() != null &&
                fall.getImpactPeakValue() != null &&
                fall.getImpactPeakDuration() != null &&
                fall.getLongestValleyValue() != null &&
                fall.getLongestValleyDuration() != null &&
                fall.getNumberOfPeaksPriorToImpact() != null &&
                fall.getNumberOfValleysPriorToImpact() != null &&
                fall.getClassificationType() != null;
    }
}
