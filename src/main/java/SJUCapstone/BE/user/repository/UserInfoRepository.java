package SJUCapstone.BE.user.repository;

import SJUCapstone.BE.user.domain.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findUserInfoByUserId(Long userId);


    @Modifying
    @Transactional
    @Query("UPDATE UserInfo u SET u.LastDiagnoseDate = :date, u.LastDiagnoseScore = :score, u.LastDiagnoseStatus = :status, u.DiagnoseNum = :num, u.userId = :uid WHERE u.userInfoId = :id")
    void updateUserInfo(@Param("date") Timestamp date, @Param("score") String score, @Param("status") Long status, @Param("num") int num, @Param("id") Long id, @Param("uid") Long uid);
    //    void updateUserInfo(@Param("id") Long id, @Param("uid") Long uid, @Param("date") Timestamp date, @Param("score") String Score, @Param("status") String status, @Param("num") int num);
}