package SJUCapstone.BE.challenge.service;

import SJUCapstone.BE.challenge.domain.Challenge;
import SJUCapstone.BE.challenge.domain.CommunityAchievement;
import SJUCapstone.BE.challenge.domain.CommunityChallenge;
import SJUCapstone.BE.challenge.dto.AddChallengeRequest;
import SJUCapstone.BE.challenge.repository.ChallengeRepository;
import SJUCapstone.BE.challenge.repository.CommunityAchievementRepository;
import SJUCapstone.BE.challenge.repository.CommunityChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private CommunityChallengeRepository communityChallengeRepository;
    @Autowired
    private CommunityAchievementRepository communityAchievementRepository;

    public void addChallengeToCommunity(AddChallengeRequest request) {
        CommunityChallenge communityChallenge = new CommunityChallenge(request.getCommunityId(), request.getChallengeId());
        communityChallengeRepository.save(communityChallenge);
    }

    public List<Challenge> getChallengesForCommunity(Long communityId) {
        List<CommunityChallenge> communityChallenges = communityChallengeRepository.findAllByCommunityId(communityId);
        List<Long> challengeIds = communityChallenges.stream()
                .map(CommunityChallenge::getChallengeId)
                .collect(Collectors.toList());

        return challengeRepository.findAllById(challengeIds);
    }

    public void deleteCommunityChallenge(Long communityChallengeId) {
        communityChallengeRepository.deleteById(communityChallengeId);
    }

    public List<Map<String, Object>> getCommunityChallenges(Long communityId) {
        List<CommunityChallenge> communityChallenges = communityChallengeRepository.findAllByCommunityId(communityId);

        // CommunityChallenge와 Challenge 정보를 결합
        List<Long> challengeIds = communityChallenges.stream()
                .map(CommunityChallenge::getChallengeId)
                .collect(Collectors.toList());

        Map<Long, String> challengeDescriptions = challengeRepository.findAllById(challengeIds).stream()
                .collect(Collectors.toMap(Challenge::getChallengeId, Challenge::getDescription));

        // 결과 생성
        List<Map<String, Object>> result = new ArrayList<>();
        for (CommunityChallenge communityChallenge : communityChallenges) {
            Map<String, Object> item = new HashMap<>();
            item.put("communityChallengeId", communityChallenge.getCommunityChallengeId());
            item.put("challengeId", communityChallenge.getChallengeId());
            item.put("description", challengeDescriptions.get(communityChallenge.getChallengeId()));
            item.put("status", communityChallenge.getStatus());
            result.add(item);
        }
        return result;
    }





    public void markChallengeSuccess(Long communityChallengeId) {
        CommunityChallenge challenge = communityChallengeRepository.findById(communityChallengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 상태 업데이트
        challenge.setStatus("SUCCESS");
        communityChallengeRepository.save(challenge);

        // 그룹 업적에 추가
        CommunityAchievement achievement = new CommunityAchievement(
                challenge.getCommunityId(),
                challenge.getChallengeId()
        );
        communityAchievementRepository.save(achievement);
        communityChallengeRepository.deleteById(communityChallengeId);
    }

    public void markChallengeFail(Long communityChallengeId) {
        CommunityChallenge challenge = communityChallengeRepository.findById(communityChallengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 상태 업데이트
        challenge.setStatus("FAILED");
        communityChallengeRepository.save(challenge);

    }

    public List<Challenge> getAchievementsByCommunity(Long communityId) {
        // 성공한 업적 조회
        List<CommunityAchievement> achievements = communityAchievementRepository.findAllByCommunityId(communityId);

        // 성공한 챌린지 ID 추출
        List<Long> challengeIds = achievements.stream()
                .map(CommunityAchievement::getChallengeId)
                .collect(Collectors.toList());

        // 성공한 챌린지 정보 반환
        return challengeRepository.findAllById(challengeIds);
    }

    public void restartChallenge(Long communityChallengeId) {
        // CommunityChallenge 엔티티 조회
        CommunityChallenge challenge = communityChallengeRepository.findById(communityChallengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 상태 확인 및 업데이트
        if (!"FAILED".equals(challenge.getStatus())) {
            throw new IllegalStateException("다시 시작할 수 있는 상태가 아닙니다. 상태: " + challenge.getStatus());
        }

        challenge.setStatus("PENDING"); // 다시 진행 상태로 변경
        communityChallengeRepository.save(challenge);
    }
}
