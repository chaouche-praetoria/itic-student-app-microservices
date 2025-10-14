package cloud.praetoria.gaming.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.services.YpareoSyncService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sync/ypareo")
@RequiredArgsConstructor
public class YpareoSyncController {

    private final YpareoSyncService ypareoSyncService;

    @GetMapping("/groups")
    public ResponseEntity<String> syncGroups() {
        ypareoSyncService.syncGroups();
        return ResponseEntity.ok("Groups synchronized");
    }

    @GetMapping("/students")
    public ResponseEntity<String> syncStudents() {
        ypareoSyncService.syncStudents();
        return ResponseEntity.ok("Students synchronized");
    }
}