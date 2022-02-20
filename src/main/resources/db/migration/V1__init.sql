CREATE TABLE users
(
    user_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name         VARCHAR(20)  NOT NULL,
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(60)  NOT NULL,
    profile_photo_url TEXT,
    url               VARCHAR(255),
    about_me          VARCHAR(255),
    account_public    BOOLEAN   DEFAULT true,
    permission        VARCHAR(20),
    created_at        TIMESTAMP DEFAULT current_timestamp,
    updated_at        TIMESTAMP DEFAULT current_timestamp,
    is_deleted        BOOLEAN   DEFAULT false
);

CREATE TABLE categories
(
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    name        VARCHAR(30) NOT NULL,
    is_public   BOOLEAN   DEFAULT true,
    color_code  VARCHAR(10),
    created_at  TIMESTAMP DEFAULT current_timestamp,
    updated_at  TIMESTAMP DEFAULT current_timestamp,
    is_deleted  BOOLEAN   DEFAULT false,
    CONSTRAINT fk_user_id_for_category FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE posts
(
    post_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    category_id   BIGINT       NOT NULL,
    selected_date DATE         NOT NULL,
    content       VARCHAR(500) NOT NULL,
    score         INT          NOT NULL,
    image_url     TEXT,
    file_url      TEXT,
    favorite      BOOLEAN   DEFAULT false,
    created_at    TIMESTAMP DEFAULT current_timestamp,
    updated_at    TIMESTAMP DEFAULT current_timestamp,
    is_deleted    BOOLEAN   DEFAULT false,
    CONSTRAINT fk_user_id_for_post FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_category_id_for_post FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE follow
(
    follow_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    CONSTRAINT fk_user_id_for_follow FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_following_id_for_follow FOREIGN KEY (following_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_user_id_and_following_id_for_follow UNIQUE (user_id, following_id)
);

CREATE TABLE likes
(
    like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    CONSTRAINT fk_user_id_for_like FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_post_id_for_like FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_user_id_and_post_id_for_like UNIQUE (user_id, post_id)
);
