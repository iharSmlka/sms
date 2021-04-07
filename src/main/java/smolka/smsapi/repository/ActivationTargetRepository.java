package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.ActivationTarget;

import java.util.List;

public interface ActivationTargetRepository extends JpaRepository<ActivationTarget, Integer> {
    List<ActivationTarget> findAll();

    ActivationTarget findByServiceCode(String serviceCode);
}
