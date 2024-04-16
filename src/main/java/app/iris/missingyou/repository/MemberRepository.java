package app.iris.missingyou.repository;

import app.iris.missingyou.entity.Member;
import app.iris.missingyou.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailAndPlatform(String email, Platform platform);

    Optional<Member> findByEmailAndPlatform(String email, Platform platform);
}
