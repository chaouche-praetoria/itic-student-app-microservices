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
    public List<TrainerDto> syncTrainersFromYpareo() {
        log.info("Starting synchronization of trainers from YParéo...");

        List<YpareoTrainerDto> remoteTrainers = ypareoClient.getAllTrainers();
        log.info("Received {} trainers from YParéo API (raw)", remoteTrainers.size());
        System.out.println("///////////////////////////////////// " + remoteTrainers);

        // Pas besoin de filtrer par email
        List<Trainer> entities = remoteTrainers.stream().map(dto -> {
            Trainer trainer = trainerRepository.findByYpareoCode(dto.getCodePersonnel())
                    .orElse(new Trainer());

            trainer.setYpareoCode(dto.getCodePersonnel());
            trainer.setFirstName(dto.getPrenomPersonnel());
            trainer.setLastName(dto.getNomPersonnel());

            return trainer;
        }).collect(Collectors.toList());

        trainerRepository.saveAll(entities);

        log.info("Synchronization complete. {} trainers updated or inserted.", entities.size());
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "trainers")
    public List<TrainerDto> getAllTrainers() {
        log.info("Fetching all trainers from database...");
        return trainerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "trainer", key = "#id")
    public Optional<TrainerDto> getTrainerById(Long id) {
        log.info("Fetching trainer by ID: {}", id);
        return trainerRepository.findById(id).map(this::toDto);
    }

    @Cacheable(value = "trainer", key = "#ypareoCode")
    public Optional<TrainerDto> getTrainerByYpareoCode(Long ypareoCode) {
        log.info("Fetching trainer by YParéo code: {}", ypareoCode);
        return trainerRepository.findByYpareoCode(ypareoCode).map(this::toDto);
    }

    private TrainerDto toDto(Trainer t) {
        return TrainerDto.builder()
                .id(t.getId())
                .ypareoCode(t.getYpareoCode())
                .firstName(t.getFirstName())
                .lastName(t.getLastName())
                .build();
    }
}
