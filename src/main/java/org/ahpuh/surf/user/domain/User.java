package org.ahpuh.surf.user.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.entity.BaseEntity;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.like.Like;
import org.ahpuh.surf.user.domain.follow.Follow;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE users SET is_deleted = 1 WHERE user_id = ?")
@Where(clause = "is_deleted = false")
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "url")
    private String url;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "account_public", columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean accountPublic = true;

    @Column(name = "permission")
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private Permission permission = Permission.ROLE_USER;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Follow> following = new ArrayList<>(); // 내가 팔로잉한

    @OneToMany(mappedBy = "followedUser", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Follow> followers = new ArrayList<>(); // 나를 팔로우한

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @Builder
    public User(final String email, final String password, final String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    public boolean checkPassword(final PasswordEncoder passwordEncoder, final String credentials) {
        if (!passwordEncoder.matches(credentials, password)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        } else {
            return true;
        }
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
    }

    public void update(final PasswordEncoder passwordEncoder, final UserUpdateRequestDto request, final String profilePhotoUrl) {
        this.userName = request.getUserName();
        this.url = request.getUrl();
        this.aboutMe = request.getAboutMe();
        this.accountPublic = request.getAccountPublic();
        if (request.getPassword() != null) {
            this.password = passwordEncoder.encode(request.getPassword());
        }
        if (profilePhotoUrl != null) {
            this.profilePhotoUrl = profilePhotoUrl;
        }
    }

    public void addCategory(final Category category) {
        categories.add(category);
    }

    public void addPost(final Post post) {
        posts.add(post);
    }

    public void addFollowing(final Follow followingUser) {
        following.add(followingUser);
    }

    public void addFollowers(final Follow follower) {
        followers.add(follower);
    }

}
