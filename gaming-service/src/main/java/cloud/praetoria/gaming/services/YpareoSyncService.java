package cloud.praetoria.gaming.services;

import java.util.List;
import java.util.Optional;

import cloud.praetoria.gaming.dtos.YpareoTrainerDto;
import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.clients.YpareoProxyClient;
import cloud.praetoria.gaming.common.FormationNormalizer;
import cloud.praetoria.gaming.dtos.YpareoGroupDto;
import cloud.praetoria.gaming.dtos.YpareoStudentDto;
import cloud.praetoria.gaming.entities.ClassGroup;
import cloud.praetoria.gaming.entities.Formation;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.enums.UserType;
import cloud.praetoria.gaming.repositories.ClassGroupRepository;
import cloud.praetoria.gaming.repositories.FormationRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class YpareoSyncService {

    private final YpareoProxyClient ypareoClient;
    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;
    private final FormationRepository formationRepository;

    @Transactional
    public void syncGroups() {
        log.info("Synchronizing groups from YParéo...");

        List<YpareoGroupDto> groups = ypareoClient.getAllGroups();
        log.info("Received {} groups from YParéo", groups.size());

        groups.forEach(dto -> {

            String formationKey = FormationNormalizer.normalize(dto.getLabel(), dto.getShortLabel());

            Formation formation = formationRepository.findByKeyName(formationKey)
                .orElseGet(() -> {
                    Formation f = new Formation();
                    f.setKeyName(formationKey);
                    f.setDisplayName(dto.getFullLabel() != null ? dto.getFullLabel() : formationKey);
                    f.setYpareoFormationCode(dto.getYpareoCode()); 
                    return formationRepository.save(f);
                });

            ClassGroup classGroup = classGroupRepository.findById(dto.getYpareoCode())
                .orElseGet(() -> {
                    ClassGroup newGroup = ClassGroup.builder()
                            .id(dto.getYpareoCode())
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
            classGroupRepository.save(classGroup);

        });

        log.info("{} groups synchronized", groups.size());
    }

    @Transactional
    public void syncStudents() {
    	log.info("Starting student sync from YParéo service...");

        List<YpareoStudentDto> students = ypareoClient.getAllStudents();
        log.info("Received {} students from YParéo", students.size());

        int created = 0;
        int updated = 0;
        int ignored = 0;

        for (YpareoStudentDto dto : students) {

            User user = userRepository.findById(dto.getYpareoCode())
                    .orElseGet(User::new);

            boolean isNew = user.getId() == null;

            user.setId(dto.getYpareoCode());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setType(UserType.STUDENT);

            userRepository.save(user);

            if (isNew) {
                created++;
            } else {
                updated++;
            }
        }

        log.info("Sync complete. Created: {}, Updated: {}, Ignored: {}", created, updated, ignored);
    }

    @Transactional
    public void syncTrainers() {
        log.info("Starting trainers sync from YParéo service...");

        List<YpareoTrainerDto> trainers = ypareoClient.getAllTrainers();
        log.info("Received {} trainers from YParéo", trainers.size());

        int created = 0;
        int updated = 0;
        int ignored = 0;

        for (YpareoTrainerDto dto : trainers) {

            // je ne pense pas que les champs soit nuls mais j'ai une erreur !!! d'où ma vérif
            if (dto.getFirstName() == null || dto.getLastName() == null) {
                log.warn("Trainer {} ignored: missing name data", dto.getYpareoCode());
                ignored++;
                continue;
            }

            Optional<User> existing = userRepository.findById(dto.getYpareoCode());
            if (existing.isPresent() && existing.get().getType() == UserType.STUDENT) {
                log.warn("User {} already exists as STUDENT, ignored for trainer sync", dto.getYpareoCode());
                ignored++;
                continue;
            }

            User user = existing.orElse(new User());
            boolean isNew = user.getId() == null;

            user.setId(dto.getYpareoCode());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setType(UserType.TRAINER);

            userRepository.save(user);

            if (isNew) created++; else updated++;
        }

        log.info("Sync complete. Created: {}, Updated: {}, Ignored: {}", created, updated, ignored);
    }
}