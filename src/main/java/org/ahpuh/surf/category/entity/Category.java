package org.ahpuh.surf.category.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.ahpuh.surf.common.entity.BaseEntity;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//@SoftDelete
@Where(clause = "is_deleted = false")
public class Category extends BaseEntity {

    @Id
    @Column(name = "category_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "average_score")
    private int averageScore;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id")
//    private User user;

//    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Post> posts = new ArrayList<>();

//    @Formula("(select count(1) from post where is_deleted = false)")
//    private int postCount;

    // Todo: user 양방향 관계 메소드 setUser(User user) {}
    //  post 양방향 관계 메소드 addPost(Post post) {}

    @Builder
    public Category(final String name, final boolean isPublic, final int averageScore, final String colorCode) {
//        this.user = user;
        this.name = name;
        this.isPublic = isPublic;
        this.colorCode = colorCode;
        this.averageScore = averageScore;
    }

    public void update(final String name, final boolean isPublic, final String colorCode) {
        this.name = name;
        this.isPublic = isPublic;
        this.colorCode = colorCode;
    }

    // Note: softDelete
//    public void delete() {
//        this.setIsDeleted(true);
//        for(Post post: posts) {
//            post.setIsDeleted = true;
//        }
//    }
}
