package app.iris.missingyou.repository;

import app.iris.missingyou.entity.Comment;
import app.iris.missingyou.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentedPostDto {
    private Post post;
    private Comment comment;
}
