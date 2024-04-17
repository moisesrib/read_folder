package oais.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegexService {

    static List<String> fichesFPS = new ArrayList<>(Arrays.asList(
            "FPS713", "FPS714", "FPS719", "FPS742", "FPS744", "FPS761",
            "FPS763", "FPS764", "FPS803", "FPS806", "FPS808", "FPS828",
            "FPS832", "FPS843", "FPS845", "FPS853", "FPS864", "FPS945", "FPS947"
    ));

    public static String typeFiche(String name) {
        return switch (name.trim()) {
            case "A" -> "Tipo 1 - Brasilia";
            case "B" -> "Tipo 2 - Brasilia";
            case "PA" -> "Tipo 1 - Pirai";
            default -> null;
        };
    }

    public static String fpsFiche(String name) {
        if (fichesFPS.contains(name.trim())) {
            return name.trim();
        }
        return null;
    }

    public static String numberFiche(String name) {
        if (name != null && name.matches("\\d{1,5}")) {
            return name.trim();
        }
        return null;
    }

    public static String numberPasep(String name) {
        if (name != null) {
            String sanitized = name.trim().replace("-", "");
            if (sanitized.matches("\\d{11}") || sanitized.matches("X{11}")) {
                return name.trim();
            }
        }
        return null;
    }

    public static String dateFiche(String year, String month, String day) {
        String fullDate = "";

        if (day != null && day.matches("\\d{1,2}")) {
            fullDate += day.trim();
        }

        if (month != null && month.matches("\\d{1,2}")) {
            if (!fullDate.isEmpty()) {
                fullDate += "/";
            }
            fullDate += month.trim();
        }

        if (year != null && year.matches("\\d{4}")) {
            int yearInt = Integer.parseInt(year.trim());

            if (yearInt >= 1971 && yearInt <= 2001) {
                if (!fullDate.isEmpty()) {
                    fullDate += "/";
                }
                fullDate += Integer.toString(yearInt);
            } else {
                fullDate = "";
            }
        } else {
            fullDate = "";
        }

        return fullDate;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
