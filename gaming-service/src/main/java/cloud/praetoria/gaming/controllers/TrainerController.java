package cloud.praetoria.gaming.controllers;

import cloud.praetoria.gaming.dtos.ClassGroupDto;
import cloud.praetoria.gaming.dtos.FormationDto;
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
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/{trainerId}/formations")
    public ResponseEntity<List<FormationDto>> getTrainerFormations(@PathVariable Long trainerId) {
        List<FormationDto> formations = trainerService.getTrainerFormations(trainerId);
        return ResponseEntity.ok(formations);
    }

    @GetMapping("/{trainerId}/class-groups")
    public ResponseEntity<List<ClassGroupDto>> getTrainerClassGroups(@PathVariable Long trainerId) {
        List<ClassGroupDto> classGroups = trainerService.getTrainerClassGroups(trainerId);
        return ResponseEntity.ok(classGroups);
    }
}
