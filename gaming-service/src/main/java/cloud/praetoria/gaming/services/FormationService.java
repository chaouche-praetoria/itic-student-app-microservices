package cloud.praetoria.gaming.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.gaming.dtos.FormationDto;
import cloud.praetoria.gaming.dtos.FormationWithStudentDto;
import cloud.praetoria.gaming.dtos.UserDto;
import cloud.praetoria.gaming.entities.ClassGroup;
import cloud.praetoria.gaming.entities.Formation;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.repositories.FormationRepository;
import cloud.praetoria.gaming.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormationService {

    private final FormationRepository formationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<FormationDto> getTrainerFormations(Long trainerId) {
        log.info("Fetching formations for trainer: {}", trainerId);
        
        List<Formation> formations = formationRepository.findByTrainerId(trainerId);
        
        log.info("Found {} formations for trainer {}", formations.size(), trainerId);
        
        return formations.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    @Transactional
	public List<UserDto> getStudentsFormation(Long formationId) {
		log.info("Getting all students for formation ID: {}", formationId);

		Formation formation = formationRepository.findById(formationId)
				.orElseThrow(() -> new RuntimeException("Formation not found with ID: " + formationId));

		List<ClassGroup> classGroups = formation.getClasses();

		if (classGroups.isEmpty()) {
			log.warn("No class groups found for formation {}", formationId);
			return List.of();
		}

		List<Long> classGroupIds = classGroups.stream().map(ClassGroup::getId).collect(Collectors.toList());

		log.info("Found {} class groups for formation {}: {}", classGroups.size(), formationId, classGroupIds);

		List<User> students = userRepository.findStudentsByClassGroupIds(classGroupIds);

		log.info("Found {} students in formation {}", students.size(), formationId);

		return students.stream().map(this::toUserDto).collect(Collectors.toList());
	}
    @Transactional
    public FormationWithStudentDto getFormationWithStudents(Long formationId) {
        log.info("Getting formation with all students for formation ID: {}", formationId);

        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation not found with ID: " + formationId));

        List<ClassGroup> classGroups = formation.getClasses();

        if (classGroups.isEmpty()) {
            log.warn("No class groups found for formation {}", formationId);
            return FormationWithStudentDto.builder()
                    .id(formation.getId())
                    .displayName(formation.getDisplayName())
                    .totalStudents(0)
                    .students(List.of())
                    .build();
        }

        List<Long> classGroupIds = classGroups.stream()
                .map(ClassGroup::getId)
                .collect(Collectors.toList());

        log.info("Found {} class groups for formation {}: {}", classGroups.size(), formationId, classGroupIds);

        List<User> students = userRepository.findStudentsByClassGroupIds(classGroupIds);

        log.info("Found {} students in formation {}", students.size(), formationId);

        List<UserDto> studentDtos = students.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());

        return FormationWithStudentDto.builder()
                .id(formation.getId())
                .displayName(formation.getDisplayName())
                .totalStudents(students.size())
                .students(studentDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public List<FormationDto> getAllFormations() {
        log.info("Fetching all formations");
        
        return formationRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private FormationDto toDto(Formation formation) {
        return FormationDto.builder()
            .id(formation.getId())
            .displayName(formation.getDisplayName())
            .build();
    }
    
    private UserDto toUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .points(user.getPoints())
            .active(user.getActive())
            .build();
    }
    private FormationWithStudentDto toFormationUsersDto(User user, Formation formation) {
    	return FormationWithStudentDto.builder()
    			.id(user.getId())
    			.displayName(formation.getDisplayName())
    			.totalStudents(formation.getClasses().size())
    			
    			.build();
    }
}
