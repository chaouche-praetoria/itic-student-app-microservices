package cloud.praetoria.gaming.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cloud.praetoria.gaming.entities.Badge;
import cloud.praetoria.gaming.enums.BadgeCategory;
import cloud.praetoria.gaming.enums.BadgeLevel;
import cloud.praetoria.gaming.enums.BadgeRarity;
import cloud.praetoria.gaming.repositories.BadgeRepository;
import jakarta.annotation.PostConstruct;

@Component
public class BadgeDataInitializer {
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @PostConstruct
    public void initBadges() {
        if (badgeRepository.count() > 0) {
            return;
        }
        
        // ========================================
        // 🎓 ACADÉMIQUES
        // ========================================
        
        // 1. S-Tier (YPAREO)
        badgeRepository.save(Badge.builder()
            .code("S_TIER")
            .name("S-Tier")
            .description("Obtenir une moyenne ≥ 16/20 sur un semestre")
            .emoji("🏅")
            .category(BadgeCategory.ACADEMIQUE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        // 2. Premier Pas (AUTO)
        badgeRepository.save(Badge.builder()
            .code("PREMIER_PAS")
            .name("Premier Pas")
            .description("Compléter son profil et se connecter pour la première fois")
            .emoji("👋")
            .category(BadgeCategory.ACADEMIQUE)
            .rarity(BadgeRarity.COMMON)
            .level(BadgeLevel.NONE)
            .xpReward(50)
            .repeatable(false)
            .build());
        
        // 3. Maître des Projets (FORMATEUR - Évolutif)
        Badge maitreProjetsBronze = badgeRepository.save(Badge.builder()
            .code("MAITRE_PROJETS_BRONZE")
            .name("Maître des Projets Bronze")
            .description("Soumettre 3 projets")
            .emoji("🚀")
            .category(BadgeCategory.ACADEMIQUE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge maitreProjetsSilver = badgeRepository.save(Badge.builder()
            .code("MAITRE_PROJETS_SILVER")
            .name("Maître des Projets Argent")
            .description("Soumettre 10 projets")
            .emoji("🚀")
            .category(BadgeCategory.ACADEMIQUE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(maitreProjetsBronze)
            .build());
        
        Badge maitreProjettsGold = badgeRepository.save(Badge.builder()
            .code("MAITRE_PROJETS_GOLD")
            .name("Maître des Projets Or")
            .description("Soumettre 20 projets")
            .emoji("🚀")
            .category(BadgeCategory.ACADEMIQUE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(maitreProjetsSilver)
            .build());
        
        maitreProjetsBronze.setNextBadge(maitreProjetsSilver);
        maitreProjetsSilver.setNextBadge(maitreProjettsGold);
        badgeRepository.save(maitreProjetsBronze);
        badgeRepository.save(maitreProjetsSilver);
        
        // ========================================
        // ⏰ GARDIENS DU TEMPS (YPAREO)
        // ========================================
        
        // 4. Ponctuel (YPAREO - Évolutif)
        Badge ponctuelBronze = badgeRepository.save(Badge.builder()
            .code("PONCTUEL_BRONZE")
            .name("Ponctuel Bronze")
            .description("1 mois sans retard")
            .emoji("🕐")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge ponctuelSilver = badgeRepository.save(Badge.builder()
            .code("PONCTUEL_SILVER")
            .name("Ponctuel Argent")
            .description("3 mois sans retard")
            .emoji("🕐")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(ponctuelBronze)
            .build());
        
        Badge ponctuelGold = badgeRepository.save(Badge.builder()
            .code("PONCTUEL_GOLD")
            .name("Ponctuel Or")
            .description("6 mois sans retard")
            .emoji("🕐")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(ponctuelSilver)
            .build());
        
        ponctuelBronze.setNextBadge(ponctuelSilver);
        ponctuelSilver.setNextBadge(ponctuelGold);
        badgeRepository.save(ponctuelBronze);
        badgeRepository.save(ponctuelSilver);
        
        // 5. Zéro Absence (YPAREO - Évolutif)
        Badge zeroAbsenceBronze = badgeRepository.save(Badge.builder()
            .code("ZERO_ABSENCE_BRONZE")
            .name("Zéro Absence Bronze")
            .description("1 semaine sans absence")
            .emoji("✅")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge zeroAbsenceSilver = badgeRepository.save(Badge.builder()
            .code("ZERO_ABSENCE_SILVER")
            .name("Zéro Absence Argent")
            .description("3 semaines sans absence")
            .emoji("✅")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(zeroAbsenceBronze)
            .build());
        
        Badge zeroAbsenceGold = badgeRepository.save(Badge.builder()
            .code("ZERO_ABSENCE_GOLD")
            .name("Zéro Absence Or")
            .description("6 semaines sans absence")
            .emoji("✅")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(zeroAbsenceSilver)
            .build());
        
        zeroAbsenceBronze.setNextBadge(zeroAbsenceSilver);
        zeroAbsenceSilver.setNextBadge(zeroAbsenceGold);
        badgeRepository.save(zeroAbsenceBronze);
        badgeRepository.save(zeroAbsenceSilver);
        
        // 6. Ultra-présent (YPAREO)
        badgeRepository.save(Badge.builder()
            .code("ULTRA_PRESENT")
            .name("Ultra-présent")
            .description("100% de présence pendant 1 an complet")
            .emoji("💯")
            .category(BadgeCategory.ASSIDUITE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(200)
            .repeatable(false)
            .build());
        
        // ========================================
        // 🤝 HÉROS DE LA COMMUNAUTÉ (FORMATEUR)
        // ========================================
        
        // 7. Mentor (FORMATEUR - Évolutif)
        Badge mentorBronze = badgeRepository.save(Badge.builder()
            .code("MENTOR_BRONZE")
            .name("Mentor Bronze")
            .description("Apporter 5 aides à d'autres étudiants")
            .emoji("🧑‍🏫")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge mentorSilver = badgeRepository.save(Badge.builder()
            .code("MENTOR_SILVER")
            .name("Mentor Argent")
            .description("Apporter 20 aides")
            .emoji("🧑‍🏫")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(mentorBronze)
            .build());
        
        Badge mentorGold = badgeRepository.save(Badge.builder()
            .code("MENTOR_GOLD")
            .name("Mentor Or")
            .description("Apporter 50 aides")
            .emoji("🧑‍🏫")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(mentorSilver)
            .build());
        
        mentorBronze.setNextBadge(mentorSilver);
        mentorSilver.setNextBadge(mentorGold);
        badgeRepository.save(mentorBronze);
        badgeRepository.save(mentorSilver);
        
        // 8. Esprit d'Équipe (FORMATEUR - Évolutif)
        Badge espritEquipeBronze = badgeRepository.save(Badge.builder()
            .code("ESPRIT_EQUIPE_BRONZE")
            .name("Esprit d'Équipe Bronze")
            .description("Réaliser 3 projets en groupe")
            .emoji("👥")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge espritEquipeSilver = badgeRepository.save(Badge.builder()
            .code("ESPRIT_EQUIPE_SILVER")
            .name("Esprit d'Équipe Argent")
            .description("Réaliser 10 projets en groupe")
            .emoji("👥")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(espritEquipeBronze)
            .build());
        
        Badge espritEquipeGold = badgeRepository.save(Badge.builder()
            .code("ESPRIT_EQUIPE_GOLD")
            .name("Esprit d'Équipe Or")
            .description("Réaliser 30 projets en groupe")
            .emoji("👥")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(espritEquipeSilver)
            .build());
        
        espritEquipeBronze.setNextBadge(espritEquipeSilver);
        espritEquipeSilver.setNextBadge(espritEquipeGold);
        badgeRepository.save(espritEquipeBronze);
        badgeRepository.save(espritEquipeSilver);
        
        // 9. Réponse Éclair (FORMATEUR - Évolutif)
        Badge reponseEclairBronze = badgeRepository.save(Badge.builder()
            .code("REPONSE_ECLAIR_BRONZE")
            .name("Réponse Éclair Bronze")
            .description("Obtenir 10 bonnes réponses en classe")
            .emoji("⚡")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge reponseEclairSilver = badgeRepository.save(Badge.builder()
            .code("REPONSE_ECLAIR_SILVER")
            .name("Réponse Éclair Argent")
            .description("Obtenir 20 bonnes réponses en classe")
            .emoji("⚡")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(reponseEclairBronze)
            .build());
        
        Badge reponseEclairGold = badgeRepository.save(Badge.builder()
            .code("REPONSE_ECLAIR_GOLD")
            .name("Réponse Éclair Or")
            .description("Obtenir 30 bonnes réponses en classe")
            .emoji("⚡")
            .category(BadgeCategory.COMMUNAUTE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(reponseEclairSilver)
            .build());
        
        reponseEclairBronze.setNextBadge(reponseEclairSilver);
        reponseEclairSilver.setNextBadge(reponseEclairGold);
        badgeRepository.save(reponseEclairBronze);
        badgeRepository.save(reponseEclairSilver);
        
        // ========================================
        // 🏆 GLOIRE ULTIME
        // ========================================
        
        // 10. Diplômé d'Honneur (FORMATEUR)
        badgeRepository.save(Badge.builder()
            .code("DIPLOME_HONNEUR")
            .name("Diplômé d'Honneur")
            .description("Obtenir le diplôme ITIC")
            .emoji("🎓")
            .category(BadgeCategory.GLOIRE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(500)
            .repeatable(false)
            .build());
        
        // 11. Champion ITIC (FORMATEUR)
        badgeRepository.save(Badge.builder()
            .code("CHAMPION_ITIC")
            .name("Champion ITIC")
            .description("Gagner une compétition ITIC")
            .emoji("🏅")
            .category(BadgeCategory.GLOIRE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(500)
            .repeatable(false)
            .build());
        
        // 12. Ambassadeur (FORMATEUR)
        badgeRepository.save(Badge.builder()
            .code("AMBASSADEUR")
            .name("Ambassadeur")
            .description("Représenter l'école ITIC")
            .emoji("🌐")
            .category(BadgeCategory.GLOIRE)
            .rarity(BadgeRarity.EPIC)
            .level(BadgeLevel.NONE)
            .xpReward(250)
            .repeatable(false)
            .build());
        
        // 13. Top 3 Promo (LEADERBOARD - AUTO)
        badgeRepository.save(Badge.builder()
            .code("TOP_3_PROMO")
            .name("Top 3 Promo")
            .description("Finir dans le top 3 de sa promotion")
            .emoji("🥇")
            .category(BadgeCategory.GLOIRE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(600)
            .repeatable(false)
            .build());
        
        // 14. Légende ITIC (META - AUTO)
        badgeRepository.save(Badge.builder()
            .code("LEGENDE_ITIC")
            .name("Légende ITIC")
            .description("Cumuler 10 000 XP au total")
            .emoji("👑")
            .category(BadgeCategory.GLOIRE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(1000)
            .repeatable(false)
            .build());
        
        // ========================================
        // ⭐ BADGES DE MAÎTRISE (META - AUTO)
        // ========================================
        
        // 15. Collectionneur (META - Évolutif)
        Badge collectionneurBronze = badgeRepository.save(Badge.builder()
            .code("COLLECTIONNEUR_BRONZE")
            .name("Collectionneur Bronze")
            .description("Obtenir 10 badges différents")
            .emoji("🎯")
            .category(BadgeCategory.MAITRISE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.BRONZE)
            .xpReward(100)
            .repeatable(false)
            .build());
        
        Badge collectionneurSilver = badgeRepository.save(Badge.builder()
            .code("COLLECTIONNEUR_SILVER")
            .name("Collectionneur Argent")
            .description("Obtenir 20 badges différents")
            .emoji("🎯")
            .category(BadgeCategory.MAITRISE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.SILVER)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(collectionneurBronze)
            .build());
        
        Badge collectionneurGold = badgeRepository.save(Badge.builder()
            .code("COLLECTIONNEUR_GOLD")
            .name("Collectionneur Or")
            .description("Obtenir 30 badges différents")
            .emoji("🎯")
            .category(BadgeCategory.MAITRISE)
            .rarity(BadgeRarity.RARE)
            .level(BadgeLevel.GOLD)
            .xpReward(100)
            .repeatable(false)
            .parentBadge(collectionneurSilver)
            .build());
        
        collectionneurBronze.setNextBadge(collectionneurSilver);
        collectionneurSilver.setNextBadge(collectionneurGold);
        badgeRepository.save(collectionneurBronze);
        badgeRepository.save(collectionneurSilver);
        
        // 16. Perfectionniste (META)
        badgeRepository.save(Badge.builder()
            .code("PERFECTIONNISTE")
            .name("Perfectionniste")
            .description("Obtenir tous les badges de niveau Or")
            .emoji("💎")
            .category(BadgeCategory.MAITRISE)
            .rarity(BadgeRarity.LEGENDARY)
            .level(BadgeLevel.NONE)
            .xpReward(800)
            .repeatable(false)
            .build());
    }
}
