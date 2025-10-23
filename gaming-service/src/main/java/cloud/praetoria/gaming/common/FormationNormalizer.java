package cloud.praetoria.gaming.common;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;


public class FormationNormalizer {

    private static final Pattern MODE_PATTERN = Pattern.compile("\\b(ALT|INIT|INITIAL|ALTERNANCE|INI)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b\\d{2}-\\d{2}\\b");
    private static final Pattern EXTRA_CHARS = Pattern.compile("[^A-Z0-9 \\-]");

    /**
     * Normalise le nom de formation pour créer une clé unique (keyName)
     * Garde la spécialité pour : M1, M2, BACH
     * Supprime la spécialité pour les autres (SISR, SLAM, SIO, etc.)
     * 
     * Exemples:
     * - "M2-BD-ALT" -> "M2-BD"
     * - "M1-CS-INIT" -> "M1-CS"
     * - "BACH CDA-ALT" -> "BACH CDA"
     * - "BACH AIS-INIT" -> "BACH AIS"
     * - "SISR2-INIT" -> "SISR2"
     * - "SLAM2-ALT" -> "SLAM2"
     */
    public static String normalize(String nomGroupe, String abregeGroupe) {
        if (nomGroupe == null && abregeGroupe == null) return "UNKNOWN";

        String src = (abregeGroupe != null && !abregeGroupe.isBlank()) ? abregeGroupe : nomGroupe;
        String s = src.toUpperCase(Locale.ROOT);

        s = YEAR_PATTERN.matcher(s).replaceAll("");
        s = MODE_PATTERN.matcher(s).replaceAll("");
        s = EXTRA_CHARS.matcher(s).replaceAll("");

        s = s.replaceAll("\\s+", "-").trim();
        s = s.replaceAll("^[-]+|[-]+$", "");
        s = s.replaceAll("-+", "-");

        if (s.startsWith("M1-") || s.startsWith("M2-") || s.startsWith("BACH-")) {
            // Pour M1-BD, M2-CS, BACH-CDA, on garde tout (les 2 premières parties)
            String[] parts = s.split("-");
            if (parts.length >= 2) {
                s = parts[0] + "-" + parts[1];
            }
        } else {
            // Pour SISR2, SLAM2, SIO1, etc., on garde seulement la première partie
            if (s.contains("-")) {
                String[] parts = s.split("-");
                s = parts[0];
            }
        }

        if (s.isBlank()) s = "UNKNOWN";
        return s;
    }

}