package app.iris.missingyou.repository;

import app.iris.missingyou.entity.Push;
import app.iris.missingyou.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushRepository extends JpaRepository<Push, Long> {
    Optional<Push> findByMemberId(Long memberId);

    @Query("SELECT p.deviceToken FROM Push p "
            +"WHERE p.region = :region AND p.deviceToken IS NOT NULL "
            + "ORDER BY p.updatedAt DESC")
    Page<String> findAllDeviceToken(Region region, Pageable pageable);
}
