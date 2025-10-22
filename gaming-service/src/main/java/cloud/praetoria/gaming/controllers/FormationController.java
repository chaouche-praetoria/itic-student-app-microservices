package cloud.praetoria.gaming.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.dtos.FormationDto;
import cloud.praetoria.gaming.dtos.UserDto;
import cloud.praetoria.gaming.services.FormationService;
import cloud.praetoria.gaming.services.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/formations")
@RequiredArgsConstructor
@Slf4j
public class FormationController {
	private final FormationService formationService;

    @GetMapping("/{formationId}/students")
    public ResponseEntity<List<UserDto>> getFormationStudents(@PathVariable Long formationId) {
        List<UserDto> students = formationService.getStudentsFormation(formationId);
        return ResponseEntity.ok(students);
    }
    
    @Operation(summary = "Lister les formations d'un formateur")
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<FormationDto>> getTrainerFormations(
            @PathVariable Long trainerId) {
        
        log.info("Request: GET /formations/trainer/{}", trainerId);
        
        List<FormationDto> formations = formationService.getTrainerFormations(trainerId);
        
        return ResponseEntity.ok(formations);
    }

    @Operation(summary = "Lister toutes les formations")
    @GetMapping
    public ResponseEntity<List<FormationDto>> getAllFormations() {
        
        log.info("Request: GET /formations");
        
        List<FormationDto> formations = formationService.getAllFormations();
        
        return ResponseEntity.ok(formations);
    }
    
    
}
