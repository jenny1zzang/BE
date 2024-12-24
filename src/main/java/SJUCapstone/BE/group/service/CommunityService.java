package SJUCapstone.BE.group.service;

import SJUCapstone.BE.group.domain.Community;
import SJUCapstone.BE.group.domain.CommunityUserInfo;
import SJUCapstone.BE.group.dto.InviteRequest;
import SJUCapstone.BE.group.repository.CommunityRepository;
import SJUCapstone.BE.group.repository.CommunityUserInfoRepository;
import SJUCapstone.BE.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private CommunityUserInfoRepository communityUserInfoRepository;

    public Long createCommunity(String communityName) {
        Community group = new Community(communityName);
        return communityRepository.save(group).getCommunityId();
    }

    public void registerCommunity(Long communityId, Long userId) {
        Long userInfoId = getUserInfoId(userId);

        communityUserInfoRepository.save(new CommunityUserInfo(communityId, userInfoId));

    }
    private Long getUserInfoId(Long userId) {
        return userInfoService.getUserInfo(userId).getUserInfoId();
    }

    public void inviteCommunity(InviteRequest request) {
        Long communityId = request.getCommunityId();
        Long userId = request.getUserId();

        registerCommunity(communityId, userId);
    }

    public List<Community> getCommunities(Long userId) {
        Long userInfoId = getUserInfoId(userId);

        // CommunityUserInfo 테이블에서 userInfoId 로 현재 가입되어 있는 CommunityUserInfo 확인
        // CommunityUserInfo로부터 communityId 만 뽑아낸다.
        List<CommunityUserInfo> communityUserInfoList = communityUserInfoRepository.findAllByUserInfoId(userInfoId);
        List<Long> communityIdList = new ArrayList<>();
        for (CommunityUserInfo communityUserInfo : communityUserInfoList) {
            communityIdList.add(communityUserInfo.getCommunityId());
        }
        // 해당 community들 List로 반환
        return communityRepository.findAllById(communityIdList);
    }

}
