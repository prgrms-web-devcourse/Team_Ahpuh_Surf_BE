package org.ahpuh.backend.user.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.ahpuh.backend.aop.SoftDelete;
import org.ahpuh.backend.common.entity.BaseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SoftDelete
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

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

//    public void addCategory(Category category) {
//        categories.add(category);
//    }

}
