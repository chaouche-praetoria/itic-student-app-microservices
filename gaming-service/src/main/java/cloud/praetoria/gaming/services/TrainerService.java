package cloud.praetoria.gaming.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cloud.praetoria.gaming.clients.YpareoProxyClient;
import cloud.praetoria.gaming.common.FormationNormalizer;
import cloud.praetoria.gaming.dtos.ClassGroupSummaryDto;
import cloud.praetoria.gaming.dtos.FormationDto;
import cloud.praetoria.gaming.dtos.UserDto;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {

	private final UserRepository userRepository;
	private final YpareoProxyClient ypareoClient;
	private final ClassGroupRepository classGroupRepository;
	private final FormationRepository formationRepository;

	

	@Transactional
	public List<FormationDto> getTrainerFormationsWithClassGroups(Long trainerId) {
		log.info("Getting formations with class groups for trainer ID: {}", trainerId);

		User trainer = userRepository.findById(trainerId)
				.orElseThrow(() -> new RuntimeException("Trainer not found with ID: " + trainerId));

		if (!trainer.isTrainer()) {
			throw new RuntimeException("User with ID " + trainerId + " is not a trainer");
		}

		List<YpareoGroupDto> trainerGroups = ypareoClient.getGroupsByTrainer(trainerId);
		log.info("Received {} groups from Ypareo for trainer {}", trainerGroups.size(), trainerId);

		if (trainerGroups.isEmpty()) {
			log.warn("No groups found in Ypareo for trainer {}", trainerId);
			return List.of();
		}

		List<ClassGroup> savedClassGroups = trainerGroups.stream().map(dto -> {
			log.debug("Processing Ypareo group: {} ({})", dto.getShortLabel(), dto.getCodeGroup());

			String formationKey = FormationNormalizer.normalize(dto.getLabel(), dto.getShortLabel());

			Formation formation = formationRepository.findByKeyName(formationKey).orElseGet(() -> {
				log.info("Creating new formation: {}", formationKey);
				Formation newFormation = new Formation();
				newFormation.setKeyName(formationKey);
				newFormation.setDisplayName(dto.getLabel() != null ? dto.getLabel() : formationKey);
				newFormation.setYpareoFormationCode(dto.getCodeGroup());
				return formationRepository.save(newFormation);
			});

			ClassGroup classGroup = classGroupRepository.findById(dto.getCodeGroup()).orElseGet(() -> {
				log.info("Creating new class group: {} ({})", dto.getShortLabel(), dto.getCodeGroup());
				ClassGroup newGroup = ClassGroup.builder().id(dto.getCodeGroup()).label(dto.getShortLabel())
						.active(true).dateDebut(dto.getDateDebut()).dateFin(dto.getDateFin()).formation(formation)
						.build();
				return classGroupRepository.save(newGroup);
			});

			classGroup.setLabel(dto.getShortLabel());
			classGroup.setFormation(formation);
			classGroup.setDateDebut(dto.getDateDebut());
			classGroup.setDateFin(dto.getDateFin());
			classGroup.setActive(true); 

			return classGroupRepository.save(classGroup);
		}).collect(Collectors.toList());

		log.info("Saved {} class groups", savedClassGroups.size());

		for (ClassGroup classGroup : savedClassGroups) {
			if (!trainer.getClassGroups().contains(classGroup)) {
				trainer.getClassGroups().add(classGroup);
				classGroup.getTrainers().add(trainer);
			}
		}
		userRepository.save(trainer);
		log.info("Linked {} class groups to trainer {}", savedClassGroups.size(), trainerId);

		Map<Long, List<ClassGroup>> classGroupsByFormation = savedClassGroups.stream()
				.collect(Collectors.groupingBy(cg -> cg.getFormation().getId()));

		log.info("Grouped class groups into {} formations", classGroupsByFormation.size());

		List<FormationDto> formationDtos = classGroupsByFormation.entrySet().stream().map(entry -> {
			List<ClassGroup> classGroups = entry.getValue();

			Formation formation = classGroups.get(0).getFormation();
			log.debug("Building DTO with {} class groups", classGroups.size());
			List<ClassGroupSummaryDto> classGroupSummaries = classGroups.stream()
					.map(cg ->
					ClassGroupSummaryDto.builder().id(cg.getId()).label(cg.getLabel())
							.totalStudents(cg.getStudents().size()).build())
					.collect(Collectors.toList());

			int totalStudents = classGroups.stream().mapToInt(cg -> cg.getStudents().size()).sum();
			boolean isActive = classGroups.stream().anyMatch(ClassGroup::isActive);

			LocalDate dateDebut = classGroups.get(0).getDateDebut();
			LocalDate dateFin = classGroups.get(0).getDateFin();

			return FormationDto.builder().id(formation.getId())
					.displayName(formation.getDisplayName())
					.totalStudents(totalStudents).dateDebut(dateDebut).dateFin(dateFin).active(isActive)
					.classGroups(classGroupSummaries).build();
		}).collect(Collectors.toList());

		log.info("Returning {} formations with class groups for trainer {}", formationDtos.size(), trainerId);
		return formationDtos;
	}

	private UserDto toUserDto(User user) {
		return UserDto.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
				.fullName(user.getFullName()).points(user.getPoints()).active(user.getActive()).build();
	}
}
