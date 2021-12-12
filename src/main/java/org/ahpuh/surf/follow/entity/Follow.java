package org.ahpuh.surf.follow.entity;

import lombok.*;
import org.ahpuh.surf.user.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "follow")
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
    @JoinColumn(name = "follower_id", referencedColumnName = "user_id")
    private User followedUser;

    @Builder
    public Follow(final User user, final User followedUser) {
        this.user = user;
        this.followedUser = followedUser;
        user.addFollowedUser(this);
        followedUser.addFollowingUser(this);
    }

}
