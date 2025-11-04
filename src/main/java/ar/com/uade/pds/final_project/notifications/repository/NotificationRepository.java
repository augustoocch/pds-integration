package ar.com.uade.pds.final_project.notifications.repository;

import ar.com.uade.pds.final_project.notifications.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Subscriber, Long> {
    List<Subscriber> findByUserId(Long userId);
}