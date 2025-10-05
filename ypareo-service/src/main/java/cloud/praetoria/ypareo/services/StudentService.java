package cloud.praetoria.ypareo.services;



import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cloud.praetoria.ypareo.clients.YpareoClient;
import cloud.praetoria.ypareo.dtos.StudentDto;
import cloud.praetoria.ypareo.dtos.YpareoStudentDto;
import cloud.praetoria.ypareo.entities.Group;
import cloud.praetoria.ypareo.entities.Student;
import cloud.praetoria.ypareo.repositories.GroupRepository;
import cloud.praetoria.ypareo.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final YpareoClient ypareoClient;

    @CacheEvict(value = {"students", "student", "students_group"}, allEntries = true)
    public List<StudentDto> syncStudentsFromYpareo() {
        log.info("Starting synchronization of students from YParéo...");

        List<YpareoStudentDto> remoteStudents = ypareoClient.getAllStudents();
        log.info("Received {} students from YParéo API", remoteStudents.size());

        List<Student> entities = remoteStudents.stream().map(dto -> {
            Group group = null;
            if (dto.getCodeGroupe() != null) {
                group = groupRepository.findByYpareoCode(dto.getCodeGroupe())
                        .orElseGet(() -> {
                            log.warn("Group not found for code {}", dto.getCodeGroupe());
                            return null;
                        });
            }

            Student student = studentRepository.findByYpareoCode(dto.getCodeApprenant())
                    .orElse(new Student());

            student.setYpareoCode(dto.getCodeApprenant());
            student.setFirstName(dto.getPrenom());
            student.setLastName(dto.getNom());
            student.setEmail(dto.getEmail());
            student.setLogin(dto.getLogin());
            student.setBirthDate(dto.getDateNaissance());
            student.setGroup(group);
            return student;
        }).collect(Collectors.toList());

        studentRepository.saveAll(entities);

        log.info("Synchronization complete. {} students updated or inserted.", entities.size());

        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Cacheable(value = "students")
    public List<StudentDto> getAllStudents() {
        log.info("Fetching all students from database...");
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "student", key = "#id")
    public Optional<StudentDto> getStudentById(Long id) {
        log.info("Fetching student by ID: {}", id);
        return studentRepository.findById(id).map(this::toDto);
    }

    @Cacheable(value = "student", key = "#ypareoCode")
    public Optional<StudentDto> getStudentByYpareoCode(Long ypareoCode) {
        log.info("Fetching student by YParéo code: {}", ypareoCode);
        return studentRepository.findByYpareoCode(ypareoCode).map(this::toDto);
    }

    @Cacheable(value = "students_group", key = "#groupId")
    public List<StudentDto> getStudentsByGroup(Long groupId) {
        log.info("Fetching students from group ID: {}", groupId);
        return studentRepository.findByGroup_Id(groupId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .ypareoCode(s.getYpareoCode())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .phone(s.getPhone())
                .city(s.getCity())
                .postalCode(s.getPostalCode())
                .address(s.getAddress())
                .groupLabel(s.getGroup() != null ? s.getGroup().getShortLabel() : null)
                .build();
    }
}
