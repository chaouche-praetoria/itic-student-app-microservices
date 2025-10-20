package cloud.praetoria.ypareo.controllers;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.ypareo.services.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/student/{userId}/no-late")
    public ResponseEntity<Boolean> hasNoLate(
        @PathVariable Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("Request: GET /api/attendance/student/{}/no-late", userId);
        Boolean noLate = attendanceService.hasNoLateBetween(userId, startDate, endDate);
        return ResponseEntity.ok(noLate);
    }

    @GetMapping("/student/{userId}/no-absence")
    public ResponseEntity<Boolean> hasNoAbsence(
        @PathVariable Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("Request: GET /api/attendance/student/{}/no-absence", userId);
        Boolean noAbsence = attendanceService.hasNoAbsenceBetween(userId, startDate, endDate);
        return ResponseEntity.ok(noAbsence);
    }

    @GetMapping("/student/{userId}/attendance-rate")
    public ResponseEntity<Double> getAttendanceRate(
        @PathVariable Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("Request: GET /api/attendance/student/{}/attendance-rate", userId);
        Double rate = attendanceService.calculateAttendanceRate(userId, startDate, endDate);
        return ResponseEntity.ok(rate);
    }
}