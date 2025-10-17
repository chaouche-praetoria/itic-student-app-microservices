// src/main/java/cloud/praetoria/gaming/controllers/AssignmentController.java
package cloud.praetoria.gaming.controllers;

import java.net.URI;
import java.util.List;

import cloud.praetoria.gaming.services.interfaces.AssignmentServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.gaming.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@io.swagger.v3.oas.annotations.tags.Tag(name = "Assignments")
public class AssignmentController {

    private final AssignmentServiceInterface assignmentServiceInterface;

    @Operation(summary = "Créer un devoir")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = AssignmentDto.class)))
    @PostMapping("/assignments")
    public ResponseEntity<AssignmentDto> create(@Valid @RequestBody AssignmentCreateRequestDto request) {
        AssignmentDto dto = assignmentServiceInterface.create(request);
        URI location = URI.create("/assignments/" + dto.getId());
        return ResponseEntity.created(location).body(dto);
    }

    @Operation(summary = "Récupérer un devoir par ID")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Devoir introuvable")
    @GetMapping("/assignments/{id}")
    public ResponseEntity<AssignmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentServiceInterface.getById(id));
    }

    @Operation(summary = "Lister les devoirs d'une classe")
    @ApiResponse(responseCode = "200")
    @GetMapping("/classes/{classGroupId}/assignments")
    public ResponseEntity<List<AssignmentDto>> listByClass(
            @Parameter(description = "ID de la classe") @PathVariable Long classGroupId) {
        return ResponseEntity.ok(assignmentServiceInterface.listByClass(classGroupId));
    }

    @Operation(summary = "Mettre à jour un devoir")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/assignments/{id}")
    public ResponseEntity<AssignmentDto> update(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentUpdateRequestDto request) {
        return ResponseEntity.ok(assignmentServiceInterface.update(id, request));
    }

    @Operation(summary = "Marquer un devoir comme complété / non complété")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/assignments/{id}/complete")
    public ResponseEntity<AssignmentDto> setCompleted(
            @PathVariable Long id,
            @RequestParam boolean completed) {
        return ResponseEntity.ok(assignmentServiceInterface.markCompleted(id, completed));
    }

    @Operation(summary = "Activer/Désactiver un devoir")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/assignments/{id}/activate")
    public ResponseEntity<AssignmentDto> setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(assignmentServiceInterface.setActive(id, active));
    }

    @Operation(summary = "Supprimer un devoir")
    @ApiResponse(responseCode = "204", description = "Supprimé (idempotent)")
    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentServiceInterface.delete(id);
        return ResponseEntity.noContent().build();
    }
}
