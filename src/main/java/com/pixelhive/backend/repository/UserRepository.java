package com.pixelhive.backend.repository;

import com.pixelhive.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// A repository is your gateway to the database for one entity.
// By extending JpaRepository<User, Long>, you get a whole set of
// ready-made methods for free: findAll(), findById(), save(), deleteById()...
// (User = the entity, Long = the type of its id.)
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring reads this METHOD NAME and writes the SQL for you:
    // "SELECT * FROM users WHERE email = ?". You never write the query.
    Optional<User> findByEmail(String email);
}
