package cloud.praetoria.ypareo.controllers;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.ypareo.dtos.GroupDto;
import cloud.praetoria.ypareo.services.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/test")
    public String test() {
        return "Group API is up ";
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        log.info("Request received: GET /groups");
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        GroupDto group = groupService.getGroupById(id);
        if (group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }

    @GetMapping("/sync")
    public ResponseEntity<List<GroupDto>> syncGroupsFromYpareo() {
        log.info("Request received: GET /groups/sync");
        List<GroupDto> synced = groupService.syncGroupsFromYpareo();
        return ResponseEntity.ok(synced);
    }
    
    @GetMapping("/trainer/sync/{id}")
    public ResponseEntity<List<GroupDto>> syncGroupsOfTrainerFromYpareo(@PathVariable Long id) {
    	System.out.println("********** test ************");
    	log.info("Request received: GET /groups/trainer/sync");
    	List<GroupDto> synced = groupService.syncGroupsOfTrainerFromYpareo(id);
    	return ResponseEntity.ok(synced);
    }
}