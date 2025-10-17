package cloud.praetoria.gaming.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.gaming.dtos.UserDto;
import cloud.praetoria.gaming.services.TrainerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/formations")
@RequiredArgsConstructor
public class FormationController {

    private final TrainerService trainerService;

    @GetMapping("/{formationId}/students")
    public ResponseEntity<List<UserDto>> getFormationStudents(@PathVariable Long formationId) {
        List<UserDto> students = trainerService.getFormationStudents(formationId);
        return ResponseEntity.ok(students);
    }
}
