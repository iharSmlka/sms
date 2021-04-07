package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserByKey(String key);

    User findUserByUserId(Long userId);

    void deleteByKey(String key);
}
