package cloud.praetoria.gaming.controllers;

import cloud.praetoria.gaming.entities.ClassGroup;
import cloud.praetoria.gaming.entities.Formation;
import cloud.praetoria.gaming.services.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/{trainerId}/class-groups")
    public ResponseEntity<List<ClassGroup>> getTrainerClassGroups(@PathVariable Long trainerId) {
        List<ClassGroup> classGroups = trainerService.getTrainerClassGroups(trainerId);
        return ResponseEntity.ok(classGroups);
    }

    @GetMapping("/{trainerId}/formations")
    public ResponseEntity<List<Formation>> getTrainerFormations(@PathVariable Long trainerId) {
        List<Formation> formations = trainerService.getTrainerFormations(trainerId);
        return ResponseEntity.ok(formations);
    }

    @GetMapping("/{trainerId}/class-groups-by-formation")
    public ResponseEntity<Map<Formation, List<ClassGroup>>> getClassGroupsByFormation(@PathVariable Long trainerId) {
        Map<Formation, List<ClassGroup>> result = trainerService.getClassGroupsByFormation(trainerId);
        return ResponseEntity.ok(result);
    }
}