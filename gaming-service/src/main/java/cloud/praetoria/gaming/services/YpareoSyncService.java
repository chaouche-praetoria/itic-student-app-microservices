package cloud.praetoria.gaming.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.clients.YpareoProxyClient;
import cloud.praetoria.gaming.common.FormationNormalizer;
import cloud.praetoria.gaming.dtos.YpareoGroupDto;
import cloud.praetoria.gaming.dtos.YpareoStudentDto;
import cloud.praetoria.gaming.dtos.YpareoTrainerDto;
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
                        f.setYpareoFormationCode(dto.getCodeGroup());
                        return formationRepository.save(f);
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
        int skipped = 0;

        for (YpareoStudentDto dto : students) {
            
            Long classGroupCode = dto.getCodeGroup();
            if (classGroupCode == null) {
                log.warn("Student {} has no class group, skipped", dto.getYpareoCode());
                skipped++;
                continue;
            }

            ClassGroup classGroup = classGroupRepository.findById(classGroupCode)
                .orElse(null);
            
            if (classGroup == null) {
                log.warn("Class group {} not found for student {}, skipped", 
                         classGroupCode, dto.getYpareoCode());
                skipped++;
                continue;
            }

            User student = userRepository.findById(dto.getYpareoCode())
                    .orElseGet(() -> {
                        User newStudent = new User();
                        newStudent.setId(dto.getYpareoCode());
                        newStudent.setType(UserType.STUDENT);
                        newStudent.setPoints(0);
                        newStudent.setActive(true);
                        newStudent.setClassGroups(new ArrayList<>()); 
                        return newStudent;
                    });

                boolean isNew = student.getFirstName() == null;

                student.setFirstName(dto.getFirstName());
                student.setLastName(dto.getLastName());
                
                if (student.getClassGroups() == null) {
                    student.setClassGroups(new ArrayList<>());
                }
                
                student.addClassGroup(classGroup);
                
                userRepository.save(student);

                if (isNew) created++; else updated++;
            }


        log.info("Sync complete. Created: {}, Updated: {}, Skipped: {}", created, updated, skipped);
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

    @Transactional
    public void syncTrainerGroups(Long trainerId) {
        log.info("Synchronizing groups for trainer ID: {}", trainerId);

        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));

        if (!trainer.isTrainer()) {
            throw new RuntimeException("User with ID " + trainerId + " is not a trainer");
        }

        List<YpareoGroupDto> trainerGroups = ypareoClient.getGroupsByTrainer(trainerId);
        log.info("Received {} groups for trainer {}", trainerGroups.size(), trainerId);

        trainerGroups.forEach(dto -> {
            String formationKey = FormationNormalizer.normalize(dto.getLabel(), dto.getShortLabel());

            Formation formation = formationRepository.findByKeyName(formationKey)
                    .orElseGet(() -> {
                        Formation f = new Formation();
                        f.setKeyName(formationKey);
                        f.setDisplayName(dto.getLabel() != null ? dto.getLabel() : formationKey);
                        f.setYpareoFormationCode(dto.getCodeGroup());
                        return formationRepository.save(f);
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
            classGroupRepository.save(classGroup);
        });

        log.info("Successfully synchronized {} groups for trainer {}", trainerGroups.size(), trainerId);
    }
}