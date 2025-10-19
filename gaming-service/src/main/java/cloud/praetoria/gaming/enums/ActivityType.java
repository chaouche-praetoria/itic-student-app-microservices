package cloud.praetoria.gaming.enums;

public enum ActivityType {
    RESOURCE_VIEW("Consultation de ressource",  5),
    RESOURCE_DOWNLOAD("Téléchargement de ressource", 10),
    RESOURCE_SHARE("Partage de ressource",  15),
    
    PROJECT_SUBMIT("Soumission de projet",  50),
    PROJECT_COMPLETE("Projet terminé", 100),
    PROJECT_EXCELLENCE("Projet d'excellence (>16/20)",  150),
    
    HELP_GIVEN("Aide apportée",  30),
    HELP_RECEIVED("Aide reçue",  10),
    FORUM_POST("Publication forum",  5),
    FORUM_ANSWER("Réponse forum", 15),
    FORUM_BEST_ANSWER("Meilleure réponse",  30),
    
    EVENT_PARTICIPATION("Participation à un événement",  20),
    WORKSHOP_ATTENDANCE("Présence à un workshop",  25),
    COMPETITION_ENTRY("Inscription à une compétition",  30),
    COMPETITION_WIN("Victoire en compétition", 200),
    
    SCHOOL_REPRESENTATION("Représentation de l'école",  100),
    AMBASSADOR("Action ambassadeur",  50),
    
    DAILY_LOGIN("Connexion quotidienne",  2),
    WEEKLY_STREAK("Série hebdomadaire",  10),
    PROFILE_COMPLETE("Profil complété",  20);
    
    private final String displayName;
    private final int baseXp;
    
    ActivityType(String displayName,  int baseXp) {
        this.displayName = displayName;
        this.baseXp = baseXp;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    
    public int getBaseXp() {
        return baseXp;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
