package cloud.praetoria.gaming.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import cloud.praetoria.gaming.services.interfaces.PointsAwardServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.gaming.dtos.GradeRequestDto;
import cloud.praetoria.gaming.dtos.GradeUpdateRequestDto;
import cloud.praetoria.gaming.dtos.PointsAwardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Grading")
public class PointsAwardController {

    private final PointsAwardServiceInterface pointsAwardServiceInterface;

    @Operation(
            summary = "Attribuer/mettre à jour la note d'un élève pour un devoir (upsert)",
            description = "Si une note existe déjà pour (assignmentId, studentId), elle est mise à jour ; sinon elle est créée."
    )
    @ApiResponse(responseCode = "201", description = "Créé ou mis à jour", content = @Content(schema = @Schema(implementation = PointsAwardDto.class)))
    @PostMapping("/{assignmentId}/grade")
    public ResponseEntity<PointsAwardDto> gradeStudent(
            @Parameter(description = "ID du devoir") @PathVariable Long assignmentId,
            @Valid @RequestBody GradeRequestDto request) {

        PointsAwardDto dto = pointsAwardServiceInterface.gradeStudent(assignmentId, request);

        // Localisation logique : /assignments/{assignmentId}/grades/{studentId}
        URI location = URI.create(String.format("/assignments/%d/grades/%d", assignmentId, dto.getStudentId()));
        // 201 si création ; 200 serait aussi acceptable en cas de mise à jour.
        return ResponseEntity.created(location).body(dto);
    }

    @Operation(summary = "Lister toutes les notes d'un devoir")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{assignmentId}/grades")
    public ResponseEntity<List<PointsAwardDto>> listAssignmentGrades(
            @Parameter(description = "ID du devoir") @PathVariable Long assignmentId) {

        return ResponseEntity.ok(pointsAwardServiceInterface.listAssignmentGrades(assignmentId));
    }

    @Operation(summary = "Récupérer la note d'un élève pour un devoir")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Note introuvable")
    @GetMapping("/{assignmentId}/grades/{studentId}")
    public ResponseEntity<PointsAwardDto> getStudentGrade(
            @Parameter(description = "ID du devoir") @PathVariable Long assignmentId,
            @Parameter(description = "ID de l'élève") @PathVariable Long studentId) {

        Optional<PointsAwardDto> opt = pointsAwardServiceInterface.getStudentGrade(assignmentId, studentId);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Mettre à jour une attribution de points (par ID)")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/grades/{awardId}")
    public ResponseEntity<PointsAwardDto> updateGrade(
            @Parameter(description = "ID de l'attribution") @PathVariable Long awardId,
            @Valid @RequestBody GradeUpdateRequestDto request) {

        return ResponseEntity.ok(pointsAwardServiceInterface.updateGrade(awardId, request));
    }

    @Operation(summary = "Supprimer une attribution de points (par ID)")
    @ApiResponse(responseCode = "204", description = "Supprimé (idempotent)")
    @DeleteMapping("/grades/{awardId}")
    public ResponseEntity<Void> deleteGrade(
            @Parameter(description = "ID de l'attribution") @PathVariable Long awardId) {

        pointsAwardServiceInterface.deleteGrade(awardId);
        return ResponseEntity.noContent().build();
    }
}
