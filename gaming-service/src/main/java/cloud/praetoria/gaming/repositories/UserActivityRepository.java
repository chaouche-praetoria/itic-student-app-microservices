package cloud.praetoria.gaming.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.gaming.entities.User;
import cloud.praetoria.gaming.entities.UserActivity;
import cloud.praetoria.gaming.enums.ActivityType;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUser(User user);
    List<UserActivity> findByUserId(Long userId);
    Long countByUserAndActivityType(User user, ActivityType activityType);
    Long countByUserIdAndActivityType(Long userId, ActivityType activityType);
}