DROP TABLE IF EXISTS follow CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    user_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name         VARCHAR(20),
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    profile_photo_url VARCHAR(255),
    url               VARCHAR(255),
    about_me          VARCHAR(255),
    account_public    BOOLEAN     DEFAULT true,
    permission        VARCHAR(20) DEFAULT "ROLE_USER",
    created_at        TIMESTAMP   DEFAULT current_time,
    updated_at        TIMESTAMP   DEFAULT current_time,
    is_deleted        BOOLEAN     DEFAULT false
);

CREATE TABLE categories
(
    category_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    name          VARCHAR(255) NOT NULL UNIQUE,
    is_public     BOOLEAN   DEFAULT true,
    average_score INTEGER   DEFAULT 0,
    color_code    VARCHAR(10),
    recent_score  INTEGER   DEFAULT 0,
    created_at    TIMESTAMP DEFAULT current_time,
    updated_at    TIMESTAMP DEFAULT current_time,
    is_deleted    BOOLEAN   DEFAULT false,
    CONSTRAINT fk_user_id_for_category FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE posts
(
    post_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    category_id   BIGINT       NOT NULL,
    selected_date VARCHAR(255) NOT NULL,
    content       VARCHAR(500) NOT NULL,
    score         INTEGER      NOT NULL,
    file_url      VARCHAR(255),
    created_at    TIMESTAMP DEFAULT current_time,
    updated_at    TIMESTAMP DEFAULT current_time,
    is_deleted    BOOLEAN   DEFAULT false,
    CONSTRAINT fk_user_id_for_post FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_category_id_for_post FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE follow
(
    follow_id    BIGINT AUTO_INCREMENT primary key,
    user_id      BIGINT,
    following_id BIGINT,
    CONSTRAINT fk_user_id_for_follow FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_following_id_for_follow FOREIGN KEY (following_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
