package cloud.praetoria.gaming.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.dtos.FormationDto;
import cloud.praetoria.gaming.services.TrainerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    
    @GetMapping("/{trainerId}/formations-with-classes")
    public ResponseEntity<List<FormationDto>> getTrainerFormationsWithClassGroups(
            @PathVariable Long trainerId) {
        List<FormationDto> formations = trainerService.getTrainerFormationsWithClassGroups(trainerId);
        return ResponseEntity.ok(formations);
    }


}
