package SJUCapstone.BE.challenge.service;

import SJUCapstone.BE.challenge.domain.Challenge;
import SJUCapstone.BE.challenge.domain.CommunityChallenge;
import SJUCapstone.BE.challenge.dto.AddChallengeRequest;
import SJUCapstone.BE.challenge.repository.ChallengeRepository;
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
            result.add(item);
        }
        return result;
    }

}
