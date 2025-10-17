package cloud.praetoria.gaming.services;

import cloud.praetoria.gaming.clients.YpareoProxyClient;
import cloud.praetoria.gaming.common.FormationNormalizer;
import cloud.praetoria.gaming.dtos.ClassGroupDto;
import cloud.praetoria.gaming.dtos.FormationDto;
import cloud.praetoria.gaming.dtos.YpareoGroupDto;
import cloud.praetoria.gaming.entities.ClassGroup;
import cloud.praetoria.gaming.entities.Formation;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.repositories.ClassGroupRepository;
import cloud.praetoria.gaming.repositories.FormationRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {

    private final UserRepository userRepository;
    private final YpareoProxyClient ypareoClient;
    private final ClassGroupRepository classGroupRepository;
    private final FormationRepository formationRepository;

    @Transactional
    public List<FormationDto> getTrainerFormations(Long trainerId) {
        log.info("Getting formations for trainer ID: {}", trainerId);

        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));

        if (!trainer.isTrainer()) {
            throw new RuntimeException("User with ID " + trainerId + " is not a trainer");
        }

        List<YpareoGroupDto> trainerGroups = ypareoClient.getGroupsByTrainer(trainerId);

        return trainerGroups.stream()
                .map(dto -> {
                    String formationKey = FormationNormalizer.normalize(dto.getLabel(), dto.getShortLabel());
                    return formationRepository.findByKeyName(formationKey)
                            .map(this::toFormationDto)
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ClassGroupDto> getTrainerClassGroups(Long trainerId) {
        log.info("Getting class groups for trainer ID: {}", trainerId);

        List<YpareoGroupDto> trainerGroups = ypareoClient.getGroupsByTrainer(trainerId);

        return trainerGroups.stream()
                .map(dto -> {
                    String formationKey = FormationNormalizer.normalize(dto.getLabel(), dto.getShortLabel());

                    Formation formation = formationRepository.findByKeyName(formationKey)
                            .orElseGet(() -> {
                                Formation newFormation = new Formation();
                                newFormation.setKeyName(formationKey);
                                newFormation.setDisplayName(dto.getLabel() != null ? dto.getLabel() : formationKey);
                                newFormation.setYpareoFormationCode(dto.getCodeGroup());
                                return formationRepository.save(newFormation);
                            });

                    ClassGroup classGroup = classGroupRepository.findById(dto.getCodeGroup())
                            .orElseGet(() -> {
                                ClassGroup newGroup = ClassGroup.builder()
                                        .id(dto.getCodeGroup())
                                        .label(dto.getShortLabel())
                                        .active(true)
                                        .dateDebut(dto.getDateDebut())
                                        .dateFin(dto.getDateFin())
                                        .formation(formation)
                                        .build();
                                return classGroupRepository.save(newGroup);
                            });

                    classGroup.setLabel(dto.getShortLabel());
                    classGroup.setFormation(formation);
                    classGroup.setDateDebut(dto.getDateDebut());
                    classGroup.setDateFin(dto.getDateFin());

                    ClassGroup savedClassGroup = classGroupRepository.save(classGroup);

                    return toClassGroupDto(savedClassGroup);
                })
                .collect(Collectors.toList());
    }

    private FormationDto toFormationDto(Formation formation) {
        return FormationDto.builder()
                .id(formation.getId())
                .keyName(formation.getKeyName())
                .displayName(formation.getDisplayName())
                .ypareoFormationCode(formation.getYpareoFormationCode())
                .build();
    }

    private ClassGroupDto toClassGroupDto(ClassGroup classGroup) {
        FormationDto formationDto = null;
        if (classGroup.getFormation() != null) {
            formationDto = toFormationDto(classGroup.getFormation());
        }
        return ClassGroupDto.builder()
                .id(classGroup.getId())
                .label(classGroup.getLabel())
                .dateDebut(classGroup.getDateDebut())
                .dateFin(classGroup.getDateFin())
                .active(classGroup.isActive())
                .formation(formationDto)
                .build();
    }
}
