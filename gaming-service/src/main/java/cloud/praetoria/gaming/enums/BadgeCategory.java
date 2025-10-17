package cloud.praetoria.gaming.enums;
public enum BadgeCategory {
    
    ACADEMIQUE("Académique", "Badges liés à la performance académique"),
    
    ASSIDUITE("Assiduité", "Badges liés à la présence et la ponctualité"),
    
    COMMUNAUTE("Communauté", "Badges liés à l'entraide et la collaboration"),
    
    GLOIRE("Gloire", "Badges liés aux accomplissements majeurs"),
    
    MAITRISE("Maîtrise", "Meta-badges de collection et progression");
    
    private final String displayName;
    private final String description;
    
    BadgeCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}