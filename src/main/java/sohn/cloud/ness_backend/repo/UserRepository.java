package sohn.cloud.ness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sohn.cloud.ness_backend.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}