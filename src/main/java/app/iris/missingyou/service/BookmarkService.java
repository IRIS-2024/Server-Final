package app.iris.missingyou.service;

import app.iris.missingyou.entity.Bookmark;
import app.iris.missingyou.entity.Member;
import app.iris.missingyou.entity.Post;
import app.iris.missingyou.exception.CustomException;
import app.iris.missingyou.repository.BookmarkRepository;
import app.iris.missingyou.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberService memberService;
    private final PostService postService;

    @Transactional
    public void create(CustomUserDetails userDetails, Long postId) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));

        Post post = postService.get(postId);


        bookmarkRepository.save(new Bookmark(member, post));
    }

    @Transactional
    public void delete(CustomUserDetails userDetails, Long postId) {
        Member member = memberService.findById(Long.parseLong(userDetails.getUsername()));

        Post post = postService.get(postId);
        if(post.getAuthor() != member)
            throw new CustomException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");

        bookmarkRepository.deleteBookmarkByMemberIdAndPostId(member.getId(), post.getId());
    }
}
