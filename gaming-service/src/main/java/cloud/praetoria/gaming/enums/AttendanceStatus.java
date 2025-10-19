package cloud.praetoria.gaming.enums;

public enum AttendanceStatus {
    PRESENT("Présent", "✅"),
    ABSENT("Absent", "❌"),
    LATE("En retard", "⏰"),
    EXCUSED_ABSENCE("Absence justifiée", "📝");
    
    private final String displayName;
    private final String emoji;
    
    AttendanceStatus(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getEmoji() {
        return emoji;
    }
    
    @Override
    public String toString() {
        return emoji + " " + displayName;
    }
}