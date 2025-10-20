package cloud.praetoria.ypareo.services;


import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cloud.praetoria.ypareo.clients.YpareoClient;
import cloud.praetoria.ypareo.dtos.YpareoAbsenceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final YpareoClient ypareoClient;

    @Cacheable(value = "attendance_no_late", key = "#userId + '_' + #startDate + '_' + #endDate")
    public Boolean hasNoLateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Checking if user {} has no late between {} and {}", userId, startDate, endDate);
        
        try {
            List<YpareoAbsenceDto> absences = ypareoClient.getAbsencesBetweenDates(startDate, endDate);
            
            if (absences == null || absences.isEmpty()) {
                return true;
            }
            
            for (YpareoAbsenceDto absence : absences) {
                if (absence.getCodeApprenant() != null && absence.getCodeApprenant().equals(userId)) {
                    if (isLateMotif(absence.getCodeMotifAbsence())) {
                        log.info("User {} has a late: motif={}", userId, absence.getCodeMotifAbsence());
                        return false;
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Error checking late for userId={}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Cacheable(value = "attendance_no_absence", key = "#userId + '_' + #startDate + '_' + #endDate")
    public Boolean hasNoAbsenceBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Checking if user {} has no absence between {} and {}", userId, startDate, endDate);
        
        try {
            List<YpareoAbsenceDto> absences = ypareoClient.getAbsencesBetweenDates(startDate, endDate);
            
            if (absences == null || absences.isEmpty()) {
                return true;
            }
            
            for (YpareoAbsenceDto absence : absences) {
                if (absence.getCodeApprenant() != null && absence.getCodeApprenant().equals(userId)) {
                    Boolean isJustified = absence.getIsJustifie();
                    if (isJustified == null || !isJustified) {
                        log.info("User {} has an unjustified absence", userId);
                        return false;
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Error checking absence for userId={}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Cacheable(value = "attendance_rate", key = "#userId + '_' + #startDate + '_' + #endDate")
    public Double calculateAttendanceRate(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating attendance rate for user {} between {} and {}", userId, startDate, endDate);
        
        try {
            List<YpareoAbsenceDto> absences = ypareoClient.getAbsencesBetweenDates(startDate, endDate);
            
            if (absences == null) {
                return 100.0;
            }
            
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            long absenceDays = 0;
            
            for (YpareoAbsenceDto absence : absences) {
                if (absence.getCodeApprenant() != null && absence.getCodeApprenant().equals(userId)) {
                    Boolean isJustified = absence.getIsJustifie();
                    if (isJustified == null || !isJustified) {
                        absenceDays++;
                    }
                }
            }
            
            if (totalDays == 0) {
                return 100.0;
            }
            
            double rate = ((totalDays - absenceDays) / (double) totalDays) * 100.0;
            log.info("User {} attendance rate: {}%", userId, rate);
            
            return Math.max(0.0, Math.min(100.0, rate));
            
        } catch (Exception e) {
            log.error("Error calculating attendance rate for userId={}: {}", userId, e.getMessage());
            return 0.0;
        }
    }

    private boolean isLateMotif(Integer motifCode) {
        if (motifCode == null) {
            return false;
        }
        return motifCode >= 10 && motifCode <= 20;
    }
}
