package smolka.smsapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.User;

import java.time.LocalDateTime;

public interface ActivationHistoryRepository extends JpaRepository<ActivationHistory, Long> {
    Page<ActivationHistory> findAllByUserAndCreateDateLessThanEqual(User user, LocalDateTime startDateTime, Pageable pageable);

    Page<ActivationHistory> findAllByUserAndCreateDateGreaterThanEqual(User user, LocalDateTime startDateTime, Pageable pageable);

    Page<ActivationHistory> findAllByUserAndCreateDateGreaterThanEqualAndCreateDateLessThanEqual(User user, LocalDateTime startDateTime1, LocalDateTime startDateTime2, Pageable pageable);

    Page<ActivationHistory> findAllByUser(Pageable pageable, User user);

    Long countByUser(User user);
}
