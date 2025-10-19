package cloud.praetoria.ypareo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.ypareo.dtos.TrainerDto;
import cloud.praetoria.ypareo.services.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @GetMapping("/code/{ypareoCode}")
    public ResponseEntity<TrainerDto> getTrainerByYpareoCode(@PathVariable Long ypareoCode) {
        return trainerService.getTrainerByYpareoCode(ypareoCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupère les informations complètes d'un formateur par son login
     * Utilisé par auth-service lors de l'inscription
     * 
     * @param login Login Ypareo du formateur (nom de famille en UPPERCASE)
     * @return TrainerDto ou 404 si non trouvé/inactif
     */
    @GetMapping("/login/{login}")
    public ResponseEntity<TrainerDto> getTrainerByLogin(@PathVariable String login) {
        log.info("Requête GET pour récupérer le formateur avec login: {}", login);
        
        Optional<TrainerDto> trainerOpt = trainerService.getTrainerByLogin(login);
        
        if (trainerOpt.isEmpty()) {
            log.warn("Formateur avec login {} non trouvé ou inactif", login);
            return ResponseEntity.notFound().build();
        }
        
        log.info("Formateur {} trouvé et renvoyé", login);
        return ResponseEntity.ok(trainerOpt.get());
    }
    
    /**
     * Vérifie si un formateur existe par son login
     * 
     * @param login Login Ypareo du formateur
     * @return {"exists": true/false}
     */
    @GetMapping("/exist/{login}")
    public ResponseEntity<Map<String, Boolean>> checkTrainerExists(@PathVariable String login) {
        log.info("Vérification existence du formateur avec login: {}", login);
        
        boolean exists = trainerService.existsByLogin(login);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        
        log.info("Formateur {} existe: {}", login, exists);
        return ResponseEntity.ok(response);
    }
    
}

