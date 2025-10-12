package cloud.praetoria.ypareo.services;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cloud.praetoria.ypareo.clients.YpareoClient;
import cloud.praetoria.ypareo.dtos.GroupDto;
import cloud.praetoria.ypareo.dtos.YpareoGroupDto;
import cloud.praetoria.ypareo.entities.Group;
import cloud.praetoria.ypareo.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final YpareoClient ypareoClient;

    @CacheEvict(value = {"groups", "group"}, allEntries = true)
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

    @Cacheable(value = "groups")
    public List<GroupDto> getAllGroups() {
        log.info("📥 Fetching all groups from database...");
        return groupRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "group", key = "#id")
    public GroupDto getGroupById(Long id) {
        return groupRepository.findById(id).map(this::toDto).orElse(null);
    }

    private GroupDto toDto(Group group) {
        return GroupDto.builder()
                .id(group.getId())
                .ypareoCode(group.getCodeGroupe())
                .label(group.getLabel())
                .shortLabel(group.getShortLabel())
                .dateDebut(group.getDateDebut())
                .dateFin(group.getDateDebut())
                .build();
    }
}