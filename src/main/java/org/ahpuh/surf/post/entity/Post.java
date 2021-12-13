package org.ahpuh.surf.post.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.common.entity.BaseEntity;
import org.ahpuh.surf.user.entity.User;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @Column(name = "selected_date", nullable = false)
    private LocalDate selectedDate;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "file_url")
    private String fileUrl;

    @Builder
    public Post(final User user, final Category category, final LocalDate selectedDate, final String content, final int score, final String fileUrl) {
        this.user = user;
        this.category = category;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
        this.fileUrl = fileUrl;
        user.addPost(this);
        category.addPost(this);
    }

    public void editPost(final Category category, final LocalDate selectedDate, final String content, final int score, final String fileUrl) {
        this.category = category;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
        this.fileUrl = fileUrl;
    }

}
