package cloud.praetoria.ypareo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cloud.praetoria.ypareo.clients.YpareoClient;
import cloud.praetoria.ypareo.dtos.TrainerDto;
import cloud.praetoria.ypareo.dtos.YpareoTrainerDto;
import cloud.praetoria.ypareo.entities.Trainer;
import cloud.praetoria.ypareo.repositories.TrainerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {
	
	 private final TrainerRepository trainerRepository;
	    private final YpareoClient ypareoClient;

	 /**
     * Synchronise tous les formateurs depuis l'API YParéo
     */
    @CacheEvict(value = {"trainers", "trainer"}, allEntries = true)
    @Transactional
    public List<TrainerDto> syncTrainersFromYpareo() {
        log.info("Starting synchronization of trainers from YParéo...");

        List<YpareoTrainerDto> remoteTrainers = ypareoClient.getAllTrainers();
        
        log.info("Received {} trainers from YParéo API (raw)", remoteTrainers.size());

        List<Trainer> entities = remoteTrainers.stream().filter(dto -> dto.getPlusUtilise()!= 1 && dto.getLogin() != null).map(dto -> {
        	
            Trainer trainer = trainerRepository.findByYpareoCode(dto.getCodePersonnel())
                    .orElse(new Trainer());
            trainer.setYpareoCode(dto.getCodePersonnel());
            trainer.setFirstName(dto.getPrenomPersonnel());
            trainer.setLastName(dto.getNomPersonnel());
            trainer.setLogin(dto.getLogin());
            
            return trainer;
        }).collect(Collectors.toList());

        trainerRepository.saveAll(entities);

        log.info("Synchronization complete. {} trainers updated or inserted.", entities.size());
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "trainers")
    @Transactional
    public List<TrainerDto> getAllTrainers() {
        log.info("Fetching all trainers from database...");
        return trainerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "trainer", key = "#id")
    @Transactional
    public Optional<TrainerDto> getTrainerById(Long id) {
        log.info("Fetching trainer by ID: {}", id);
        return trainerRepository.findById(id).map(this::toDto);
    }

    @Cacheable(value = "trainer", key = "#ypareoCode")
    @Transactional
    public Optional<TrainerDto> getTrainerByYpareoCode(Long ypareoCode) {
        log.info("Fetching trainer by YParéo code: {}", ypareoCode);
        return trainerRepository.findByYpareoCode(ypareoCode).map(this::toDto);
    }
    /**
     * Récupère un formateur par son login Ypareo
     * Filtre automatiquement les formateurs avec plusUtilise = 1
     */
    public Optional<TrainerDto> getTrainerByLogin(String login) {
        log.info("Récupération du formateur avec login: {}", login);
        
        
        Optional<Trainer> trainerOpt = trainerRepository.findByLoginIgnoreCase(login);
        
        if (trainerOpt.isEmpty()) {
            log.warn("Aucun formateur trouvé avec le login: {}", login);
            return Optional.empty();
        }
        
        Trainer trainer = trainerOpt.get();
        
        
        log.info("Formateur {} trouvé et actif", login);
        return Optional.of(toDto(trainer));
    }
    
    /**
     * Vérifie si un formateur existe par son login (actif uniquement)
     */
    public boolean existsByLogin(String login) {
        Optional<Trainer> trainerOpt = trainerRepository.findByLoginIgnoreCase(login);
        
        if (trainerOpt.isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    // Méthode de conversion existante
    private TrainerDto toDto(Trainer trainer) {
        return TrainerDto.builder()
                .id(trainer.getYpareoCode())
                .ypareoCode(trainer.getYpareoCode())
                .login(trainer.getLogin())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .build();
    }
}