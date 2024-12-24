package SJUCapstone.BE.challenge.service;

import SJUCapstone.BE.challenge.domain.Challenge;
import SJUCapstone.BE.challenge.dto.CreateChallengeRequest;
import SJUCapstone.BE.challenge.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeAdminService {

    @Autowired
    private ChallengeRepository challengeRepository;

    public void createChallenge(CreateChallengeRequest request) {
        Challenge challenge = new Challenge();
        challenge.setDescription(request.getDescription());
        challengeRepository.save(challenge);
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public void deleteChallenge(Long challengeId) {
        if (!challengeRepository.existsById(challengeId)) {
            throw new IllegalArgumentException("챌린지가 존재하지 않습니다.");
        }
        challengeRepository.deleteById(challengeId);
    }
}
