package cloud.praetoria.gaming.enums;

public enum BadgeLevel {
    
    BRONZE("Bronze", 1.0),
    
    SILVER("Argent", 1.5),
    
    GOLD("Or", 2.0);
    
    private final String displayName;
    private final double xpMultiplier;
    
    BadgeLevel(String displayName, double xpMultiplier) {
        this.displayName = displayName;
        this.xpMultiplier = xpMultiplier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getXpMultiplier() {
        return xpMultiplier;
    }
    
    public BadgeLevel getNext() {
        return switch (this) {
            case BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD -> null;
        };
    }
    
    public boolean hasNext() {
        return this != GOLD;
    }
    
    public boolean isHigherThan(BadgeLevel other) {
        return this.xpMultiplier > other.xpMultiplier;
    }
    
    public String toString() {
        return displayName;
    }
}