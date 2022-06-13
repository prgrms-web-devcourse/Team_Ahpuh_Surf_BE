package org.ahpuh.surf.post.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.domain.BaseEntity;
import org.ahpuh.surf.common.exception.post.FavoriteInvalidUserException;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.domain.FileType;
import org.ahpuh.surf.user.domain.User;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "favorite", columnDefinition = "boolean default false")
    private Boolean favorite = false;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<Like> likes = new ArrayList<>();

    @Builder
    public Post(final User user, final Category category, final LocalDate selectedDate, final String content, final int score) {
        this.user = user;
        this.category = category;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
        user.getPosts().add(this);
        category.getPosts().add(this);
    }

    public void updatePost(final Category category, final LocalDate selectedDate, final String content, final int score) {
        this.category = category;
        this.selectedDate = selectedDate;
        this.content = content;
        this.score = score;
    }

    public void updateFile(final FileStatus fileStatus) {
        if (fileStatus.fileType().equals(FileType.IMAGE)) {
            this.imageUrl = fileStatus.fileUrl();
            this.fileUrl = null;
        } else if (fileStatus.fileType().equals(FileType.FILE)) {
            this.fileUrl = fileStatus.fileUrl();
            this.imageUrl = null;
        }
    }

    public void updateFavorite(final Long userId) {
        if (!user.getUserId().equals(userId)) {
            throw new FavoriteInvalidUserException();
        }
        favorite = !favorite;
    }
}
