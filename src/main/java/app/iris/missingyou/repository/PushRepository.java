package app.iris.missingyou.repository;

import app.iris.missingyou.entity.Push;
import app.iris.missingyou.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushRepository extends JpaRepository<Push, Long> {
    Optional<Push> findByMemberId(Long memberId);

    @Query("SELECT DISTINCT push.deviceToken FROM Push push " +
            "INNER JOIN Post post ON push.member.id = post.author.id " +
            "WHERE post.id = :pid ")
    Optional<String> findPostAuthorDeviceToken(@Param("pid") Long pid);

    @Query("SELECT p.deviceToken FROM Push p "
            +"WHERE p.region = :region AND p.deviceToken IS NOT NULL AND p.member.id <> :memberId "
            + "ORDER BY p.updatedAt DESC")
    Page<String> findAllDeviceToken(@Param("region") Region region, @Param("memberId") Long memberId, Pageable pageable);
}
