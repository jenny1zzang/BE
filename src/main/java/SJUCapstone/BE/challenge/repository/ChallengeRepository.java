package SJUCapstone.BE.challenge.repository;

import SJUCapstone.BE.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
