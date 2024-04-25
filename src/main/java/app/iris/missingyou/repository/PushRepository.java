package app.iris.missingyou.repository;

import app.iris.missingyou.entity.Push;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushRepository extends JpaRepository<Push, Long> {
    Optional<Push> findByMemberId(Long memberId);
}
