package cloud.praetoria.ypareo.controllers;

import cloud.praetoria.ypareo.dtos.TrainerDto;
import cloud.praetoria.ypareo.services.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/sync")
    public ResponseEntity<List<TrainerDto>> syncTrainersFromYpareo() {
        log.info("Request received: GET /trainers/sync");
        return ResponseEntity.ok(trainerService.syncTrainersFromYpareo());
    }

    @GetMapping
    public ResponseEntity<List<TrainerDto>> getAllTrainers() {
        return ResponseEntity.ok(trainerService.getAllTrainers());
    }

    @GetMapping("/{ypareoCode}")
    public ResponseEntity<TrainerDto> getTrainerByYpareoCode(@PathVariable Long ypareoCode) {
        return trainerService.getTrainerByYpareoCode(ypareoCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

