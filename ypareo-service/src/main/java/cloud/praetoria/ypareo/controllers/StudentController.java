package cloud.praetoria.ypareo.controllers;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.ypareo.dtos.StudentDto;
import cloud.praetoria.ypareo.services.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/students")
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

    @GetMapping("/sync")
    public ResponseEntity<List<StudentDto>> syncStudentsFromYpareo() {
        log.info("Request received: GET /students/sync");
        List<StudentDto> synced = studentService.syncStudentsFromYpareo();
        return ResponseEntity.ok(synced);
    }
}
