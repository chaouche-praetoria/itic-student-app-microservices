package cloud.praetoria.gaming.enums;

public enum BadgeLevel {
    
    BRONZE("Bronze", "🥉", 1, "#CD7F32", 1.0),
    
    SILVER("Argent", "🥈", 2, "#C0C0C0", 1.5),
    
    GOLD("Or", "🥇", 3, "#FFD700", 2.0);
    
    private final String displayName;
    private final String icon;
    private final int tier;
    private final String colorHex;
    private final double xpMultiplier;
    
    BadgeLevel(String displayName, String icon, int tier, String colorHex, double xpMultiplier) {
        this.displayName = displayName;
        this.icon = icon;
        this.tier = tier;
        this.colorHex = colorHex;
        this.xpMultiplier = xpMultiplier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public int getTier() {
        return tier;
    }
    
    public String getColorHex() {
        return colorHex;
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
        return this.tier > other.tier;
    }
    
    @Override
    public String toString() {
        return icon + " " + displayName;
    }
}