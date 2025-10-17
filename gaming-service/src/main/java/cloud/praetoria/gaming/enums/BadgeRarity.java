package cloud.praetoria.gaming.enums;


public enum BadgeRarity {
    
    COMMON("Commun", 1.0),
    
    RARE("Rare",  1.5),
    
    EPIC("Épique", 2.0),
    
    LEGENDARY("Légendaire",  3.0);
    
    private final String displayName;
    private final double xpMultiplier;
    
    BadgeRarity(String displayName, double xpMultiplier) {
        this.displayName = displayName;
        this.xpMultiplier = xpMultiplier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    
    public double getXpMultiplier() {
        return xpMultiplier;
    }
    
    public BadgeRarity getNext() {
        return switch (this) {
            case COMMON -> RARE;
            case RARE -> EPIC;
            case EPIC -> LEGENDARY;
            case LEGENDARY -> null;
        };
    }
    
    public boolean isHigherThan(BadgeRarity other) {
        return this.xpMultiplier > other.xpMultiplier;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
