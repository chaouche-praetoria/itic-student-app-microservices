package cloud.praetoria.ypareo.controllers;


import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.ypareo.entities.Session;
import cloud.praetoria.ypareo.services.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    /**
     * 🔹 Synchronize all sessions for a specific student between two dates.
     *
     * Example:
     * GET /api/sessions/sync/student/123?start=2025-09-01&end=2025-10-15
     */
    @GetMapping("/sync/{studentId}")
    public ResponseEntity<String> syncSessionsForStudent(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        log.info("API request: Sync sessions for student {} from {} to {}", studentId, start, end);
        int count = sessionService.syncSessionsForStudent(studentId, start, end);
        return ResponseEntity.ok(String.format("%d sessions synchronized for student %d", count, studentId));
    }


    /**
     * 🔹 Get all sessions stored in the database.
     *
     * Example:
     * GET /api/sessions
     */
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        log.info("API request: Fetch all sessions");
        List<Session> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * 🔹 Get all sessions for a specific student.
     *
     * Example:
     * GET /api/sessions/student/123
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Session>> getSessionsByStudent(@PathVariable Long studentId) {
        log.info("API request: Fetch sessions for student {}", studentId);
        List<Session> sessions = sessionService.getAllSessions().stream()
                .filter(s -> s.getCodeApprenant().equals(studentId))
                .toList();
        return ResponseEntity.ok(sessions);
    }

}