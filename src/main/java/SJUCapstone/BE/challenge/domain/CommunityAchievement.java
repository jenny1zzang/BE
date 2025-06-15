package SJUCapstone.BE.challenge.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CommunityAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    private Long communityId;
    private Long challengeId;

    public CommunityAchievement() {}

    public CommunityAchievement(Long communityId, Long challengeId) {
        this.communityId = communityId;
        this.challengeId = challengeId;
    }
}
