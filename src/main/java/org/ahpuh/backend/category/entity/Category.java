package org.ahpuh.backend.category.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.ahpuh.backend.aop.SoftDelete;
import org.ahpuh.backend.common.entity.BaseEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "category")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SoftDelete
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

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

    public void update(String name, boolean isPublic, String colorCode) {
        this.name = name;
        this.isPublic = isPublic;
        this.colorCode = colorCode;
    }

    // Note: softDelete
    public void delete() {
        this.setIsDeleted(true);
//        for(Post post: posts) {
//            post.setIsDeleted = true;
//        }
    }

    @Builder
    public Category(String name, boolean isPublic, int averageScore, String colorCode) {
//        this.user = user;
        this.name = name;
        this.isPublic = isPublic;
        this.colorCode = colorCode;
        this.averageScore = averageScore;
    }
}
