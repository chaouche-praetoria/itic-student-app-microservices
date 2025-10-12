package cloud.praetoria.gaming.common;

import java.util.Locale;
import java.util.regex.Pattern;

public class FormationNormalizer {

    private static final Pattern MODE_PATTERN = Pattern.compile("\\b(ALT|INIT|INITIAL|ALTERNANCE|INI)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b\\d{2}-\\d{2}\\b");
    private static final Pattern EXTRA_CHARS = Pattern.compile("[^A-Z0-9 \\-]");

    public static String normalize(String nomGroupe, String abregeGroupe) {
        if (nomGroupe == null && abregeGroupe == null) return "UNKNOWN";

        String src = (abregeGroupe != null && !abregeGroupe.isBlank()) ? abregeGroupe : nomGroupe;
        String s = src.toUpperCase(Locale.ROOT);

        s = YEAR_PATTERN.matcher(s).replaceAll("");
        s = MODE_PATTERN.matcher(s).replaceAll("");

        s = EXTRA_CHARS.matcher(s).replaceAll("");

        s = s.replaceAll("\\s+", " ").trim();
        s = s.replaceAll("^[-\\s]+|[-\\s]+$", "");

        if (s.contains("-")) {
            String[] parts = s.split("-");
            for (String p : parts) {
                String trimmed = p.trim();
                if (trimmed.matches(".*[A-Z].*")) {
                    s = trimmed;
                    break;
                }
            }
        }

        if (s.isBlank()) s = "UNKNOWN";
        return s;
    }
}
