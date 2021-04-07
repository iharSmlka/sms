package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CurrentActivationRepository extends JpaRepository<CurrentActivation, Long> {

    @EntityGraph(value = "currentActivation.all")
    @Query(value = "SELECT act FROM CurrentActivation act WHERE act.user = ?1 AND (act.status = 0 OR act.status = 1)")
    List<CurrentActivation> findAllCurrentActivationsByUser(User user);

    @EntityGraph(value = "currentActivation.all")
    List<CurrentActivation> findAllCurrentActivationsByStatus(Integer status);

    @EntityGraph(value = "currentActivation.all")
    @Query(value = "SELECT act FROM CurrentActivation act WHERE act.plannedFinishDate <= ?1 AND (act.status = 0 OR act.status = 1)")
    List<CurrentActivation> findAllCurrentActivationsByPlannedFinishDateLessThanEqual(LocalDateTime dateTime);

    @EntityGraph(value = "currentActivation.all")
    CurrentActivation findCurrentActivationByIdAndUser(Long id, User user);
}
