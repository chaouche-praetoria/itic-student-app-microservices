package cloud.praetoria.gaming.enums;

public enum BadgeRarity {
    
    COMMON("Commun", "⚪", 1, "#9E9E9E", 1.0),
    
    RARE("Rare", "🔵", 2, "#2196F3", 1.5),
    
    EPIC("Épique", "🟣", 3, "#9C27B0", 2.0),
    
    LEGENDARY("Légendaire", "🟡", 4, "#FFC107", 3.0);
    
    private final String displayName;
    private final String icon;
    private final int tier;
    private final String colorHex;
    private final double xpMultiplier;
    
    BadgeRarity(String displayName, String icon, int tier, String colorHex, double xpMultiplier) {
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
    
    public BadgeRarity getNext() {
        return switch (this) {
            case COMMON -> RARE;
            case RARE -> EPIC;
            case EPIC -> LEGENDARY;
            case LEGENDARY -> null;
        };
    }
    
    public boolean isHigherThan(BadgeRarity other) {
        return this.tier > other.tier;
    }
    
    @Override
    public String toString() {
        return icon + " " + displayName;
    }
}