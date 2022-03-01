package org.ahpuh.surf.post.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.entity.BaseEntity;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.post.domain.like.Like;
import org.ahpuh.surf.s3.FileStatus;
import org.ahpuh.surf.user.domain.User;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@SQLDelete(sql = "UPDATE posts SET is_deleted = true WHERE post_id = ?")
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

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "favorite")
    @Builder.Default
    private Boolean favorite = false;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Post(final User user, final Category category, final LocalDate selectedDate, final String content, final int score) {
        this.user = user;
        this.category = category;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
        favorite = false;
        user.addPost(this);
        category.addPost(this);
    }

    public void editPost(final Category category, final LocalDate selectedDate, final String content, final int score) {
        this.category = category;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
    }

    public Post editFile(final FileStatus fileStatus) {
        if (fileStatus.fileType().equals("img")) {
            this.imageUrl = fileStatus.fileUrl();
            this.fileUrl = null;
        }
        if (fileStatus.fileType().equals("file")) {
            this.fileUrl = fileStatus.fileUrl();
            this.imageUrl = null;
        }
        return this;
    }

    public void updateFavorite(final Long userId) {
        if (!user.getUserId().equals(userId)) {
            throw EntityExceptionHandler.UserNotMatching(user.getUserId(), userId);
        }
        favorite = !favorite;
    }

    public void addLike(final Like like) {
        likes.add(like);
    }

}