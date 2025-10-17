package cloud.praetoria.ypareo.services;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.ypareo.clients.YpareoClient;
import cloud.praetoria.ypareo.dtos.YpareoCourseDto;
import cloud.praetoria.ypareo.entities.Session;
import cloud.praetoria.ypareo.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

	    private final SessionRepository sessionRepository;
	    private final YpareoClient ypareoClient;

	    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	    @CacheEvict(value = "sessions", allEntries = true)
	    @Transactional
	    public int syncSessionsForStudent(Long studentId, LocalDate startDate, LocalDate endDate) {
	        log.info("Starting session sync for student {} between {} and {}", studentId, startDate, endDate);

	        List<YpareoCourseDto> remoteSessions = ypareoClient.getSessionsForStudent(studentId, startDate, endDate);
	        if (remoteSessions.isEmpty()) {
	            log.warn("No sessions to sync for student {}", studentId);
	            return 0;
	        }
	        
	        List<Session> entities = remoteSessions.stream()
	                .map(this::toEntity)
	                .collect(Collectors.toList());

	        sessionRepository.saveAll(entities);

	        log.info("{} sessions synchronized for student {}", entities.size(), studentId);
	        return entities.size();
	    }

	    @Cacheable(value = "sessions")
	    public List<Session> getAllSessions() {
	        log.info("Fetching all sessions from database...");
	        return sessionRepository.findAll();
	    }

	    private Session toEntity(YpareoCourseDto dto) {
	        Session s = new Session();

	        s.setCodeApprenant(dto.getCodeApprenant());
	        s.setNomMatiere(dto.getNomMatiere());

	        try {
	            s.setDate(LocalDate.parse(dto.getDate(), DATE_FORMATTER));
	            s.setHeureDebut(java.time.LocalTime.parse(dto.getHeureDebut(), TIME_FORMATTER));
	            s.setHeureFin(java.time.LocalTime.parse(dto.getHeureFin(), TIME_FORMATTER));
	        } catch (Exception e) {
	            log.warn("Failed to parse date/time for session {}", dto.getNomMatiere());
	        }

	        s.setDuree(dto.getDuree());
	        s.setCodeSalle(dto.getCodesSalle() != null && !dto.getCodesSalle().isEmpty() ? dto.getCodesSalle().get(0) : null);
	        s.setIsDistance(dto.getIsDistance() != null && dto.getIsDistance() == 1);


	        return s;
	    }
}
