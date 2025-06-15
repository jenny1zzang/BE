package SJUCapstone.BE.challenge.repository;

import SJUCapstone.BE.challenge.domain.CommunityAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityAchievementRepository extends JpaRepository<CommunityAchievement, Long> {
    List<CommunityAchievement> findAllByCommunityId(Long communityId);
}
