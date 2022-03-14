package org.ahpuh.surf.user.domain;

import lombok.*;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.domain.BaseEntity;
import org.ahpuh.surf.common.exception.user.InvalidPasswordException;
import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE users SET is_deleted = 1 WHERE user_id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "users")
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
    private Boolean accountPublic;

    @Column(name = "permission")
    @Enumerated(value = EnumType.STRING)
    private Permission permission;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Category> categories;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "source", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Follow> following; // 내가 팔로잉한

    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Follow> followers; // 나를 팔로우한

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Like> likes;

    @Formula("(select count(1) from follow f where f.user_id = user_id)")
    private int followingCount;

    @Formula("(select count(1) from follow f where f.following_id = user_id)")
    private int followerCount;

    @Builder
    public User(final String email, final String password, final String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        accountPublic = true;
        permission = Permission.ROLE_USER;
        categories = new ArrayList<>();
        posts = new ArrayList<>();
        following = new ArrayList<>();
        followers = new ArrayList<>();
        likes = new ArrayList<>();
    }

    public boolean checkPassword(final PasswordEncoder passwordEncoder, final String credentials) {
        if (Objects.isNull(credentials)) {
            throw new InvalidPasswordException();
        }
        if (!passwordEncoder.matches(credentials, password)) {
            throw new InvalidPasswordException();
        } else {
            return true;
        }
    }

    public void setPermission(final Permission permission) {
        this.permission = permission;
    }

    public void update(final PasswordEncoder passwordEncoder, final UserUpdateRequestDto request, final Optional<String> profilePhotoUrl) {
        if (isNotEmpty(request.getPassword())) {
            this.password = passwordEncoder.encode(request.getPassword());
        }
        this.userName = request.getUserName();
        this.url = request.getUrl();
        this.aboutMe = request.getAboutMe();
        this.accountPublic = request.getAccountPublic();
        profilePhotoUrl.ifPresent(s -> this.profilePhotoUrl = s);
    }

    public void addCategory(final Category category) {
        if (Objects.isNull(categories)) {
            categories = new ArrayList<>();
        }
        categories.add(category);
    }

    public void addPost(final Post post) {
        if (Objects.isNull(posts)) {
            posts = new ArrayList<>();
        }
        posts.add(post);
    }

    public void addFollowing(final Follow followingUser) {
        if (Objects.isNull(following)) {
            following = new ArrayList<>();
        }
        following.add(followingUser);
    }

    public void addFollowers(final Follow follower) {
        if (Objects.isNull(followers)) {
            followers = new ArrayList<>();
        }
        followers.add(follower);
    }

    public void addLike(final Like like) {
        if (Objects.isNull(likes)) {
            likes = new ArrayList<>();
        }
        likes.add(like);
    }
}
