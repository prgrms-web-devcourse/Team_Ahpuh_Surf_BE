package org.ahpuh.surf.follow.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ahpuh.surf.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "follow",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "following_id"}
                )
        }
)
public class Follow {

    @Id
    @Column(name = "follow_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", referencedColumnName = "user_id")
    private User target;

    @Builder
    public Follow(final User source, final User target) {
        this.source = source;
        this.target = target;
        source.getFollowing().add(this);
        target.getFollowers().add(this);
    }
}
