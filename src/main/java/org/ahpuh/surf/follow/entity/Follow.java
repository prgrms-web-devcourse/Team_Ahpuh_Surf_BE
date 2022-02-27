package org.ahpuh.surf.follow.entity;

import lombok.*;
import org.ahpuh.surf.user.domain.User;

import javax.persistence.*;

@Entity
@Table(
        name = "follow",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "following_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Follow {

    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", referencedColumnName = "user_id")
    private User followedUser;

    @Builder
    public Follow(final User user, final User followedUser) {
        this.user = user;
        this.followedUser = followedUser;
        user.addFollowing(this);
        followedUser.addFollowers(this);
    }

}
