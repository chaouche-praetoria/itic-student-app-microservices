package cloud.praetoria.ypareo.services;



import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cloud.praetoria.ypareo.clients.YpareoClient;
import cloud.praetoria.ypareo.dtos.FormationGroupDto;
import cloud.praetoria.ypareo.dtos.FormationGroupDto.SubGroupDto;
import cloud.praetoria.ypareo.dtos.GroupDto;
import cloud.praetoria.ypareo.dtos.YpareoGroupDto;
import cloud.praetoria.ypareo.dtos.YpareoGroupTrainerDto;
import cloud.praetoria.ypareo.entities.Group;
import cloud.praetoria.ypareo.repositories.GroupRepository;
import cloud.praetoria.ypareo.repositories.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final YpareoClient ypareoClient;
    private final StudentRepository studentRepository;

    @Transactional
    @CacheEvict(value = {"groups", "group", "teacher-groups"}, allEntries = true)
    public List<GroupDto> syncGroupsFromYpareo() {
        log.info("Starting synchronization of groups from YParéo...");

        List<YpareoGroupDto> remoteGroups = ypareoClient.getAllGroups();
        log.info("Received {} groups from YParéo API", remoteGroups.size());

        List<Group> entities = remoteGroups.stream().map(dto -> {
        	Group group = groupRepository.findByCodeGroupe(dto.getCodeGroupe())
        		    .orElseGet(() -> {
        		        log.warn("Group {} not found. Creating a placeholder group.", dto.getCodeGroupe());
        		        Group placeholder = Group.builder()
        		                .codeGroupe(dto.getCodeGroupe())
        		                .label("Unknown group " + dto.getCodeGroupe())
        		                .shortLabel("N/A")
        		                .build();
        		        return groupRepository.save(placeholder);
        		    });
        	
            group.setCodeGroupe(dto.getCodeGroupe());
            group.setLabel(dto.getNomGroupe());
            group.setShortLabel(dto.getAbregeGroupe());
			
            group.setDateDebut(dto.getDateDebut());
            group.setDateFin(dto.getDateFin());
            return group;
        }).collect(Collectors.toList());

        groupRepository.saveAll(entities);
        log.info("Groups synchronization complete. {} groups updated or inserted.", entities.size());

        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    @Cacheable(value = "teacher-groups")
    public List<GroupDto> syncGroupsOfTrainerFromYpareo(Long trainerId) {
        log.info("Starting synchronization of groups of trainer {} from YParéo", trainerId);

        List<YpareoGroupTrainerDto> remoteGroups = ypareoClient.getGroupsOfTrainer(trainerId);
        log.info("Received {} groups from YParéo API for trainer {}", remoteGroups.size(), trainerId);

        if (remoteGroups.isEmpty()) {
            log.warn("No groups found for trainer {}", trainerId);
            return Collections.emptyList();
        }

        List<Long> codeGroupes = remoteGroups.stream()
            .map(YpareoGroupTrainerDto::getCodeGroupe)
            .distinct()
            .collect(Collectors.toList());

        List<Group> existingGroups = groupRepository.findByCodeGroupeIn(codeGroupes);
        
        List<Long> foundCodes = existingGroups.stream()
            .map(Group::getCodeGroupe)
            .collect(Collectors.toList());
        
        List<Long> missingCodes = codeGroupes.stream()
            .filter(code -> !foundCodes.contains(code))
            .collect(Collectors.toList());

        if (!missingCodes.isEmpty()) {
            log.warn("Trainer {} has {} groups not yet synchronized in database: {}", 
                     trainerId, missingCodes.size(), missingCodes);
            log.info("Suggestion: Run full groups synchronization to import these groups");
        }

        log.info("Returning {} existing groups for trainer {} (ignored {} missing)", 
                 existingGroups.size(), trainerId, missingCodes.size());

        return existingGroups.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }


    @Cacheable(value = "groups")
    public List<GroupDto> getAllGroups() {
        log.info("Fetching all groups from database...");
        return groupRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "group", key = "#id")
    public GroupDto getGroupById(Long id) {
        return groupRepository.findById(id).map(this::toDto).orElse(null);
    }
    
    /**
     * Récupère les formations d'un formateur en regroupant ALT et INIT
     * Utilise syncGroupsOfTrainerFromYpareo() en interne
     * 
     * @param trainerId Code Ypareo du formateur
     * @return Liste des formations regroupées avec sous-groupes ALT/INIT
     */
    @Cacheable(value = "teacher-formations")
    public List<FormationGroupDto> getGroupedFormationsByTrainer(Long trainerId) {
        log.info("Récupération des formations regroupées (ALT/INIT) pour le formateur: {}", trainerId);
        
        List<GroupDto> allGroupDtos = syncGroupsOfTrainerFromYpareo(trainerId);
        
        if (allGroupDtos.isEmpty()) {
            log.warn("Aucun groupe trouvé pour le formateur: {}", trainerId);
            return Collections.emptyList();
        }
        
        log.info("Trouvé {} groupes avant regroupement", allGroupDtos.size());
        
        List<Long> groupIds = allGroupDtos.stream()
            .map(GroupDto::getId)
            .collect(Collectors.toList());
        
        List<Group> groups = groupRepository.findAllById(groupIds);
        
        Map<String, List<Group>> groupedByFormation = groups.stream()
            .collect(Collectors.groupingBy(this::extractFormationKey));
        
        log.info("Regroupé en {} formations distinctes", groupedByFormation.size());
        
        List<FormationGroupDto> formations = groupedByFormation.entrySet().stream()
            .map(entry -> buildFormationDto(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(FormationGroupDto::getShortLabel))
            .collect(Collectors.toList());
        
        log.info("Retour de {} formations regroupées", formations.size());
        
        return formations;
    }
    
    /**
     * Extrait la clé de formation en enlevant ALT/INIT du label
     * 
     * Exemples:
     * - "25-26 ALT-M1-MPI-BD" → "M1-MPI-BD"
     * - "25-26 INIT-BTS2 SLAM" → "BTS2 SLAM"
     */
    private String extractFormationKey(Group group) {
        String label = group.getLabel();
        
        Pattern pattern = Pattern.compile("^\\d{2}-\\d{2}\\s+(ALT-|INIT-)?(.+)$");
        Matcher matcher = pattern.matcher(label);
        
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        
        return label;
    }
    
    /**
     * Extrait le type (ALT ou INIT) depuis le label
     */
    private String extractGroupType(Group group) {
        String label = group.getLabel();
        
        if (label.contains("ALT-")) {
            return "ALT";
        } else if (label.contains("INIT-")) {
            return "INIT";
        }
        
        String shortLabel = group.getShortLabel();
        if (shortLabel != null) {
            if (shortLabel.endsWith("-ALT")) {
                return "ALT";
            } else if (shortLabel.endsWith("-INIT") || shortLabel.endsWith("-INI")) {
                return "INIT";
            }
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Construit un FormationGroupDto à partir d'une liste de groupes (ALT + INIT)
     */
    private FormationGroupDto buildFormationDto(String formationKey, List<Group> groups) {
        Group firstGroup = groups.get(0);
        
        List<SubGroupDto> subGroups = groups.stream()
            .map(group -> {
                Integer studentCount = studentRepository.findByGroup_Id(group.getId()).size();
                
                return SubGroupDto.builder()
                    .id(group.getId())
                    .type(extractGroupType(group))
                    .codeGroup(group.getCodeGroupe())
                    .studentCount(studentCount)
                    .build();
            })
            .sorted(Comparator.comparing(SubGroupDto::getType)) 
            .collect(Collectors.toList());
        
        Integer totalStudents = subGroups.stream()
            .mapToInt(SubGroupDto::getStudentCount)
            .sum();
        
        String cleanShortLabel = firstGroup.getShortLabel()
            .replaceAll("-(ALT|INIT|INI)$", "");
        
        return FormationGroupDto.builder()
            .formationId(formationKey)
            .label("25-26 " + formationKey)
            .shortLabel(cleanShortLabel)
            .dateDebut(firstGroup.getDateDebut())
            .dateFin(firstGroup.getDateFin())
            .groups(subGroups)
            .totalStudents(totalStudents)
            .build();
    }

    private GroupDto toDto(Group group) {
        return GroupDto.builder()
                .id(group.getId())
                .CodeGroup(group.getCodeGroupe())
                .label(group.getLabel())
                .shortLabel(group.getShortLabel())
                .dateDebut(group.getDateDebut())
                .dateFin(group.getDateFin())
                .build();
    }
}