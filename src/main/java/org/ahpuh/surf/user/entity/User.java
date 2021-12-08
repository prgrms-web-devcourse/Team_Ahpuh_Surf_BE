package org.ahpuh.surf.user.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.ahpuh.surf.aop.SoftDelete;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.common.entity.BaseEntity;
import org.ahpuh.surf.post.entity.Post;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
@SoftDelete
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
    private String permission;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @Builder
    public User(final String userName, final String email, final String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public void checkPassword(final PasswordEncoder passwordEncoder, final String credentials) {
        if (!passwordEncoder.matches(credentials, password))
            throw new IllegalArgumentException("Bad credential");
    }

    public List<GrantedAuthority> getAuthority() {
        return List.of(new SimpleGrantedAuthority(permission));
    }

    public void setPermission(final String permission) {
        this.permission = permission;
    }

    public void addCategory(final Category category) {
        categories.add(category);
    }

    public void addPost(final Post post) {
        posts.add(post);
    }

}
