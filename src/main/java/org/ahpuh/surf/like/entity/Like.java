package org.ahpuh.surf.like.entity;

import lombok.*;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.user.entity.User;

import javax.persistence.*;

@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "post_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Like {

    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @Builder
    public Like(final User user, final Post post) {
        this.user = user;
        this.post = post;
        post.addLike(this);
    }

}
