package cloud.praetoria.gaming.enums;

public enum EventType {
    COURS("Cours", "📚"),
    TP("Travaux Pratiques", "💻"),
    TD("Travaux Dirigés", "📝"),
    PROJET("Projet", "🚀"),
    EXAMEN("Examen", "📋"),
    CONFERENCE("Conférence", "🎤"),
    WORKSHOP("Workshop", "🛠️");
    
    private final String displayName;
    private final String emoji;
    
    EventType(String displayName, String emoji) {
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
