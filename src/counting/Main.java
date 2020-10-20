package counting;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Path.of("/home/cimb/Documents/personal/counting/input/12.10.2020-18.10.2020.csv"));
        List<LocalDateTime> dateTimes = new ArrayList<>();
        for (String line : lines) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy,HH:mm");
            dateTimes.add(LocalDateTime.parse(line, formatter));
        }
        Collections.sort(dateTimes);

        int minHour = -1;
        int maxHour = -1;
        Map<LocalDate, Map<Integer, Integer>> counting = new LinkedHashMap<>();
        for (LocalDateTime dateTime : dateTimes) {
            LocalDate date = dateTime.toLocalDate();
            Map<Integer, Integer> hours = counting.get(date);
            if (hours == null) {
                counting.put(date, new LinkedHashMap<>());
            }
            hours = counting.get(date);

            int hour = dateTime.getHour();
            if (minHour == -1) {
                minHour = hour;
            }
            if (hour < minHour) {
                minHour = hour;
            }
            if (hour > maxHour) {
                maxHour = hour;
            }

            Integer count = hours.get(hour);
            if (count == null) {
                hours.put(hour, 1);
            } else {
                hours.put(hour, count + 1);
            }
        }

        List<String> result = new ArrayList<>();
        StringBuilder header = new StringBuilder("");
        for (LocalDate date : counting.keySet()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            header.append("," + date.format(formatter));
        }
        result.add(header.toString());

        for (int i = minHour; i <= maxHour; i++) {
            StringBuilder content = new StringBuilder("" + i);
            for (Map<Integer, Integer> hours : counting.values()) {
                Integer hour = hours.get(i);
                if (hour == null) {
                    hour = 0;
                }

                content.append("," + hour);
            }
            result.add(content.toString());
        }

        for (String content : result) {
            System.out.println(content);
        }
    }
}
