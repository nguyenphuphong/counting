package counting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final String INPUT_FOLDER = "input";
    private static final String OUTPUT_FOLDER = "output";
    private static final String SEPARATOR = ";";

    public static void main(String[] args) throws Exception {
        List<String> fileToCounts = getNewFileToCounts();
        for (String filename : fileToCounts) {
            System.out.println("Generating " + filename);

            List<String> lines = Files.readAllLines(Path.of("input" + File.separator + filename));
            List<LocalDateTime> dateTimes = extractDateTimeGroupByInvoiceNo(lines);
            Map<LocalDate, Map<Integer, Integer>> countingMap = countByDateEachHour(dateTimes);
            writeCountingAsTableToFile(countingMap, filename);

            System.out.println("Generated " + filename);
        }
    }

    private static List<String> getNewFileToCounts() {
        List<String> outputFilenames = new ArrayList<>();
        File outputFolder = new File(OUTPUT_FOLDER);
        if (outputFolder.listFiles() != null) {
            for (File file : outputFolder.listFiles()) {
                outputFilenames.add(file.getName());
            }
        }

        List<String> fileToCounts = new ArrayList<>();
        File inputFolder = new File(INPUT_FOLDER);
        if (inputFolder.listFiles() != null) {
            for (File file : inputFolder.listFiles()) {
                if (!outputFilenames.contains(file.getName())) {
                    fileToCounts.add(file.getName());
                }
            }
        }

        return fileToCounts;
    }

    private static List<LocalDateTime> extractDateTimeGroupByInvoiceNo(List<String> lines) {
        Map<String, LocalDateTime> inverseMapGroupByInvoiceNo = new LinkedHashMap<>();
        for (String line : lines) {
            String substring = line.substring(0, line.lastIndexOf(SEPARATOR));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy" + SEPARATOR + "HH:mm");

            inverseMapGroupByInvoiceNo.put(line.substring(line.lastIndexOf(SEPARATOR)),
                    LocalDateTime.parse(substring, formatter));
        }

        List<LocalDateTime> dateTimes = new ArrayList<>(inverseMapGroupByInvoiceNo.values());
        Collections.sort(dateTimes);
        return dateTimes;
    }

    private static Map<LocalDate, Map<Integer, Integer>> countByDateEachHour(List<LocalDateTime> dateTimes) {
        // date, hour, count
        Map<LocalDate, Map<Integer, Integer>> counting = new LinkedHashMap<>();
        for (LocalDateTime dateTime : dateTimes) {
            LocalDate date = dateTime.toLocalDate();
            Map<Integer, Integer> hours = counting.get(date);
            if (hours == null) {
                counting.put(date, new LinkedHashMap<>());
            }
            hours = counting.get(date);

            int hour = dateTime.getHour();
            Integer count = hours.get(hour);
            if (count == null) {
                hours.put(hour, 1);
            } else {
                hours.put(hour, count + 1);
            }
        }

        return counting;
    }

    private static int getMinHour(Map<LocalDate, Map<Integer, Integer>> countingMap) {
        int min = 25;
        for (Map<Integer, Integer> hourMap : countingMap.values()) {
            for (Integer hour : hourMap.keySet()) {
                if (hour < min) {
                    min = hour;
                }
            }
        }

        return min;
    }

    private static int getMaxHour(Map<LocalDate, Map<Integer, Integer>> countingMap) {
        int max = -1;
        for (Map<Integer, Integer> hourMap : countingMap.values()) {
            for (Integer hour : hourMap.keySet()) {
                if (hour > max) {
                    max = hour;
                }
            }
        }

        return max;
    }

    private static void writeCountingAsTableToFile(Map<LocalDate, Map<Integer, Integer>> countingMap, String filename)
            throws IOException {
        FileWriter writer = new FileWriter("output" + File.separator + filename);

        StringBuilder header = new StringBuilder("");
        for (LocalDate date : countingMap.keySet()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            header.append(SEPARATOR + date.format(formatter));
        }
        writer.write(header.toString() + System.lineSeparator());

        int minHour = getMinHour(countingMap);
        int maxHour = getMaxHour(countingMap);
        for (int i = minHour; i <= maxHour; i++) {
            StringBuilder content = new StringBuilder(i + ":00 - " + (i + 1) + ":00");
            for (Map<Integer, Integer> hours : countingMap.values()) {
                Integer hour = hours.get(i);
                if (hour == null) {
                    hour = 0;
                }

                content.append(SEPARATOR + hour);
            }
            writer.write(content.toString() + System.lineSeparator());
        }

        writer.close();
    }
}
