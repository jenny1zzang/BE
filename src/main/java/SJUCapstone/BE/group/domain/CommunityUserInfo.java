package SJUCapstone.BE.group.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CommunityUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityUserInfoId;

    private Long communityId;
    private Long userInfoId;

    public CommunityUserInfo(Long communityId, Long userInfoId) {
        this.communityId = communityId;
        this.userInfoId = userInfoId;
    }

}
