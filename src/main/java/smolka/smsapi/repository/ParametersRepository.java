package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.InternalParameter;

public interface ParametersRepository extends JpaRepository<InternalParameter, String> {
    InternalParameter findByName(String name);
}
