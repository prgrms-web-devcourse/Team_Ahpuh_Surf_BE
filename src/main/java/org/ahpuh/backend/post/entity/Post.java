package org.ahpuh.backend.post.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ahpuh.backend.category.entity.Category;
import org.ahpuh.backend.common.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    /*
    TODO: 카테고리가 user_id를 가지고 있는데 post도 user_id를 가지고 있어야 할까?
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @Column(name = "selected_date", nullable = false)
    private LocalDate selectedDate;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "file_url")
    private String fileUrl;

    public void editPost(final Category category, final LocalDate selectedDate, final String title, final String content,
                         final int score, final String fileUrl) {
        this.category = category;
        this.selectedDate = selectedDate;
        this.title = title;
        this.content = content;
        this.score = score;
        this.fileUrl = fileUrl;
    }

}
