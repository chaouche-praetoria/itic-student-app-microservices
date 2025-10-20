package cloud.praetoria.ypareo.controllers;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.ypareo.dtos.StudentDto;
import cloud.praetoria.ypareo.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;
    
    @GetMapping("/test")
    public String testApi() {
    	return "ok";
    		}

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        log.info("Request received: GET /students");
        List<StudentDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ypareo/{ypareoCode}")
    public ResponseEntity<StudentDto> getStudentByYpareoCode(@PathVariable Long ypareoCode) {
        return studentService.getStudentByYpareoCode(ypareoCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentDto> getStudentByEmail(@PathVariable String email) {
    	return studentService.getStudentByYpareoEmail(email)
    			.map(ResponseEntity::ok)
    			.orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/login/{login}")
    public ResponseEntity<StudentDto> getStudentByLogin(@PathVariable String login) {
    	return studentService.getStudentByYpareoLogin(login)
    			.map(ResponseEntity::ok)
    			.orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sync")
    public ResponseEntity<List<StudentDto>> syncStudentsFromYpareo() {
        log.info("Request received: GET /students/sync");
        List<StudentDto> synced = studentService.syncStudentsFromYpareo();
        return ResponseEntity.ok(synced);
    }

	/**
	 * Récupère tous les étudiants d'un groupe par son ID interne GET
	 * /students/group/{groupId}
	 * 
	 * @param groupId L'ID interne du groupe (pas le ypareoCode)
	 * @return Liste des étudiants du groupe
	 */
	@GetMapping("/group/{groupId}")
	public ResponseEntity<List<StudentDto>> getStudentsByGroup(@PathVariable Long groupId) {
		log.info("Request received: GET /students/group/{}", groupId);
		List<StudentDto> students = studentService.getStudentsByGroup(groupId);

		if (students.isEmpty()) {
			log.warn("No students found for group ID: {}", groupId);
		} else {
			log.info("Found {} students for group ID: {}", students.size(), groupId);
		}

		return ResponseEntity.ok(students);
	}
	
	/**
	 * Récupère les étudiants de plusieurs groupes (pour une formation ALT + INIT)
	 * GET /api/ypareo/students/formation?groupIds=38,40
	 */
	@GetMapping("/formation")
	public ResponseEntity<List<StudentDto>> getStudentsByFormation(
	        @RequestParam List<Long> groupIds) {
	    
	    log.info("Request: GET /students/formation?groupIds={}", groupIds);
	    
	    // Récupérer et fusionner les étudiants de tous les groupes
	    List<StudentDto> allStudents = groupIds.stream()
	        .flatMap(groupId -> studentService.getStudentsByGroup(groupId).stream())
	        .sorted(Comparator.comparing(StudentDto::getLastName)
	                          .thenComparing(StudentDto::getFirstName))
	        .collect(Collectors.toList());
	    
	    log.info("Found {} total students across {} groups", allStudents.size(), groupIds.size());
	    
	    return ResponseEntity.ok(allStudents);
	}
	
	
    
}
