package cloud.praetoria.gaming.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cloud.praetoria.gaming.entities.ClassGroup;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
    List<ClassGroup> findByActiveTrue();
}