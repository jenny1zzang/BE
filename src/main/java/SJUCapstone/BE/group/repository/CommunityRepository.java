package SJUCapstone.BE.group.repository;

import SJUCapstone.BE.group.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    public List<Community> findByCommunityIdIn(List<Long> communityIdList);

}
