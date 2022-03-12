package org.ahpuh.surf.category.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ahpuh.surf.common.domain.BaseEntity;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE category_id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "color_code")
    private String colorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts;

    @Formula("(select count(1) from posts p where p.category_id = category_id and p.is_deleted = false)")
    private int postCount;

    @Builder
    public Category(final User user, final String name, final String colorCode) {
        this.user = user;
        this.name = name;
        this.colorCode = colorCode;
        isPublic = true;
        posts = new ArrayList<>();
        user.addCategory(this);
    }

    public void update(final String name, final boolean isPublic, final String colorCode) {
        this.name = name;
        this.isPublic = isPublic;
        this.colorCode = colorCode;
    }

    public void addPost(final Post post) {
        if (posts.isEmpty()) {
            posts = new ArrayList<>();
        }
        posts.add(post);
    }
}
