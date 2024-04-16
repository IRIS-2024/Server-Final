package app.iris.missingyou.repository;

import app.iris.missingyou.entity.Bookmark;
import app.iris.missingyou.entity.Member;
import app.iris.missingyou.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository <Bookmark, Long> {
    boolean existsBookmarkByMemberAndPost(Member member, Post post);

    void deleteBookmarkByMemberIdAndPostId(Long memberId, Long postId);

    void deleteAllByPostId(Long postId);
}
