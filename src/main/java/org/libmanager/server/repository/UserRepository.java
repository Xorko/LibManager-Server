package org.libmanager.server.repository;

import java.util.Optional;

import org.libmanager.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findUserByUsernameAndPassword(String username, String password);

}
