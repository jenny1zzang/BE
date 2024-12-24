package SJUCapstone.BE.group.repository;

import SJUCapstone.BE.group.domain.CommunityUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityUserInfoRepository extends JpaRepository<CommunityUserInfo, Long> {

    public List<CommunityUserInfo> findAllByUserInfoId(Long userInfoId);
}
