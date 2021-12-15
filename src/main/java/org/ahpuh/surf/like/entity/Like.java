package org.ahpuh.surf.like.entity;

import lombok.*;

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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @Builder
    public Like(final Long userId, final Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

}
