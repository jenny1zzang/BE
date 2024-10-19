package SJUCapstone.BE.user.repository;

import SJUCapstone.BE.user.domain.User;
import SJUCapstone.BE.user.dto.UserUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.name = :name, u.age = :age, u.gender = :gender WHERE u.userId = :id")
    int updateUser(@Param("id") Long id, @Param("name") String name, @Param("age") int age, @Param("gender") String gender);
}
