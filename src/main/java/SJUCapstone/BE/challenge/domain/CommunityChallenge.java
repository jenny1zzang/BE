package SJUCapstone.BE.challenge.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CommunityChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityChallengeId;

    private Long communityId;
    private Long challengeId;

    public CommunityChallenge() {}

    public CommunityChallenge(Long communityId, Long challengeId) {
        this.communityId = communityId;
        this.challengeId = challengeId;
    }
}
