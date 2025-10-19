package cloud.praetoria.gaming.schedulers;

import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.repositories.UserRepository;
import cloud.praetoria.gaming.services.BadgeEvaluationService;
import cloud.praetoria.gaming.services.UserBadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class YpareoBadgeSyncScheduler {

    private final UserRepository userRepository;
    private final BadgeEvaluationService badgeEvaluationService;
    private final UserBadgeService userBadgeService;
    private final RestTemplate restTemplate;
    
    private static final String YPAREO_SERVICE_URL = "http://ypareo-service:8082";

    @Scheduled(cron = "0 0 2 * * *")
    public void syncYpareoBadgesDaily() {
        log.info("Démarrage de la synchronisation Ypareo pour les badges...");
        
        List<User> allStudents = userRepository.findAll();
        int badgesAwarded = 0;
        
        for (User student : allStudents) {
            badgesAwarded += evaluateAllYpareoBadges(student.getId());
        }
        
        log.info("Synchronisation Ypareo terminée. {} badges attribués.", badgesAwarded);
    }

    private int evaluateAllYpareoBadges(Long userId) {
        int count = 0;
        
        count += evaluateSTierBadge(userId);
        count += evaluatePonctuelBadges(userId);
        count += evaluateZeroAbsenceBadges(userId);
        count += evaluateUltraPresentBadge(userId);
        
        return count;
    }

    private int evaluateSTierBadge(Long userId) {
        try {
            String url = YPAREO_SERVICE_URL + "/api/grades/student/" + userId + "/semesters";
            List<Map<String, Object>> grades = restTemplate.getForObject(url, List.class);
            
            if (grades != null) {
                for (Map<String, Object> grade : grades) {
                    Double average = (Double) grade.get("averageGrade");
                    if (average != null && average >= 16.0 && !userBadgeService.userHasBadge(userId, "S_TIER")) {
                        badgeEvaluationService.evaluateAndAwardBadge(userId, "S_TIER");
                        log.info("Badge S-Tier attribué à l'étudiant {}", userId);
                        return 1;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notes pour l'étudiant {}: {}", userId, e.getMessage());
        }
        
        return 0;
    }

    private int evaluatePonctuelBadges(Long userId) {
        int count = 0;
        LocalDate now = LocalDate.now();
        
        try {
            LocalDate oneMonthAgo = now.minusMonths(1);
            Boolean hasNoLateOneMonth = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/no-late?startDate=" + oneMonthAgo + "&endDate=" + now,
                Boolean.class
            );
            
            if (Boolean.TRUE.equals(hasNoLateOneMonth) && !userBadgeService.userHasBadge(userId, "PONCTUEL_BRONZE")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "PONCTUEL_BRONZE");
                log.info("Badge Ponctuel Bronze attribué à l'étudiant {}", userId);
                count++;
            }
            
            LocalDate threeMonthsAgo = now.minusMonths(3);
            Boolean hasNoLateThreeMonths = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/no-late?startDate=" + threeMonthsAgo + "&endDate=" + now,
                Boolean.class
            );
            
            if (Boolean.TRUE.equals(hasNoLateThreeMonths) && !userBadgeService.userHasBadge(userId, "PONCTUEL_SILVER")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "PONCTUEL_SILVER");
                log.info("Badge Ponctuel Argent attribué à l'étudiant {}", userId);
                count++;
            }
            
            LocalDate sixMonthsAgo = now.minusMonths(6);
            Boolean hasNoLateSixMonths = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/no-late?startDate=" + sixMonthsAgo + "&endDate=" + now,
                Boolean.class
            );
            
            if (Boolean.TRUE.equals(hasNoLateSixMonths) && !userBadgeService.userHasBadge(userId, "PONCTUEL_GOLD")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "PONCTUEL_GOLD");
                log.info("Badge Ponctuel Or attribué à l'étudiant {}", userId);
                count++;
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'évaluation Ponctuel pour l'étudiant {}: {}", userId, e.getMessage());
        }
        
        return count;
    }

    private int evaluateZeroAbsenceBadges(Long userId) {
        int count = 0;
        LocalDate now = LocalDate.now();
        
        try {
            LocalDate oneWeekAgo = now.minusWeeks(1);
            Boolean hasNoAbsenceOneWeek = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/no-absence?startDate=" + oneWeekAgo + "&endDate=" + now,
                Boolean.class
            );
            
            if (Boolean.TRUE.equals(hasNoAbsenceOneWeek) && !userBadgeService.userHasBadge(userId, "ZERO_ABSENCE_BRONZE")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "ZERO_ABSENCE_BRONZE");
                log.info("Badge Zéro Absence Bronze attribué à l'étudiant {}", userId);
                count++;
            }
            
            LocalDate threeWeeksAgo = now.minusWeeks(3);
            Boolean hasNoAbsenceThreeWeeks = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/no-absence?startDate=" + threeWeeksAgo + "&endDate=" + now,
                Boolean.class
            );
            
            if (Boolean.TRUE.equals(hasNoAbsenceThreeWeeks) && !userBadgeService.userHasBadge(userId, "ZERO_ABSENCE_SILVER")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "ZERO_ABSENCE_SILVER");
                log.info("Badge Zéro Absence Argent attribué à l'étudiant {}", userId);
                count++;
            }
            
            LocalDate sixWeeksAgo = now.minusWeeks(6);
            Boolean hasNoAbsenceSixWeeks = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/no-absence?startDate=" + sixWeeksAgo + "&endDate=" + now,
                Boolean.class
            );
            
            if (Boolean.TRUE.equals(hasNoAbsenceSixWeeks) && !userBadgeService.userHasBadge(userId, "ZERO_ABSENCE_GOLD")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "ZERO_ABSENCE_GOLD");
                log.info("Badge Zéro Absence Or attribué à l'étudiant {}", userId);
                count++;
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'évaluation Zéro Absence pour l'étudiant {}: {}", userId, e.getMessage());
        }
        
        return count;
    }

    private int evaluateUltraPresentBadge(Long userId) {
        try {
            LocalDate now = LocalDate.now();
            LocalDate oneYearAgo = now.minusYears(1);
            
            Double attendanceRate = restTemplate.getForObject(
                YPAREO_SERVICE_URL + "/api/attendance/student/" + userId + "/attendance-rate?startDate=" + oneYearAgo + "&endDate=" + now,
                Double.class
            );
            
            if (attendanceRate != null && attendanceRate >= 100.0 && !userBadgeService.userHasBadge(userId, "ULTRA_PRESENT")) {
                badgeEvaluationService.evaluateAndAwardBadge(userId, "ULTRA_PRESENT");
                log.info("Badge Ultra-présent attribué à l'étudiant {}", userId);
                return 1;
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'évaluation Ultra-présent pour l'étudiant {}: {}", userId, e.getMessage());
        }
        
        return 0;
    }

    @Scheduled(cron = "0 0 3 * * SUN")
    public void weeklyBadgeCheck() {
        log.info("Vérification hebdomadaire des badges...");
        syncYpareoBadgesDaily();
    }
}
