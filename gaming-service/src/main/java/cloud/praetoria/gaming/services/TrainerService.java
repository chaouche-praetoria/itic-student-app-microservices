package cloud.praetoria.gaming.services;

import cloud.praetoria.gaming.entities.ClassGroup;
import cloud.praetoria.gaming.entities.Formation;
import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final UserRepository userRepository;

    public List<ClassGroup> getTrainerClassGroups(Long trainerId) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        if (!trainer.isTrainer()) {
            throw new RuntimeException("User is not a trainer");
        }

        return trainer.getClassGroups();
    }

    public List<Formation> getTrainerFormations(Long trainerId) {
        List<ClassGroup> classGroups = getTrainerClassGroups(trainerId);

        return classGroups.stream()
                .map(ClassGroup::getFormation)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<Formation, List<ClassGroup>> getClassGroupsByFormation(Long trainerId) {
        List<ClassGroup> classGroups = getTrainerClassGroups(trainerId);

        return classGroups.stream()
                .collect(Collectors.groupingBy(ClassGroup::getFormation));
    }
}
