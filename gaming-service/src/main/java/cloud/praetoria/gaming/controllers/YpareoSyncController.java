package cloud.praetoria.gaming.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.services.YpareoSyncService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sync/ypareo")
@RequiredArgsConstructor
public class YpareoSyncController {

    private final YpareoSyncService ypareoSyncService;

    @PostMapping("/groups")
    public ResponseEntity<String> syncGroups() {
        ypareoSyncService.syncGroups();
        return ResponseEntity.ok("Groups synchronized");
    }

    @PostMapping("/students")
    public ResponseEntity<String> syncStudents() {
        ypareoSyncService.syncStudents();
        return ResponseEntity.ok("Students synchronized");
    }

    @PostMapping("/trainers")
    public ResponseEntity<String> syncTrainers() {
        ypareoSyncService.syncTrainers();
        return ResponseEntity.ok("Trainers synchronized");
    }

    @PostMapping("/trainer/{trainerId}/class-groups")
    public ResponseEntity<String> syncTrainerGroups(@PathVariable Long trainerId) {
        ypareoSyncService.syncTrainerGroups(trainerId);
        return ResponseEntity.ok("Trainer groups synchronized successfully");
    }
}