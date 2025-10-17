package cloud.praetoria.ypareo.repositories;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.ypareo.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Find a course by its YParéo unique code.
     * @param ypareoCode the YParéo course/session code
     * @return Optional<Course>
     */
    Optional<Course> findByYpareoCode(Long ypareoCode);

    /**
     * Find all courses for a given group within a specific date range.
     * Used to build a group's schedule.
     */
    List<Course> findByGroup_IdAndStartTimeBetween(Long groupId, LocalDateTime start, LocalDateTime end);

    /**
     * Find all courses for a given trainer within a date range.
     * Used for a trainer's timetable.
     */
    List<Course> findByYpareoCodeAndStartTimeBetween(Long trainerId, LocalDateTime start, LocalDateTime end);

}
