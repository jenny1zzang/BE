package SJUCapstone.BE.auth.repository;

import SJUCapstone.BE.auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

}
