package SJUCapstone.BE.challenge.repository;

import SJUCapstone.BE.challenge.domain.CommunityChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityChallengeRepository extends JpaRepository<CommunityChallenge, Long> {
    List<CommunityChallenge> findAllByCommunityId(Long communityId);
}
