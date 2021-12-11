DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    user_id           bigint auto_increment primary key,
    user_name         varchar(20),
    email             varchar(255) NOT NULL unique,
    password          varchar(255) NOT NULL,
    profile_photo_url varchar(255),
    url               varchar(255),
    about_me          varchar(255),
    account_public    boolean default true,
    permission        varchar(20),
    created_at        timestamp,
    updated_at        timestamp,
    is_deleted        boolean
);

CREATE TABLE categories
(
    category_id   bigint auto_increment primary key,
    user_id       bigint,
    name          varchar(255) NOT NULL unique,
    is_public     boolean default true,
    average_score integer default 0,
    color_code    varchar(10),
    recent_score  integer default 0,
    created_at    timestamp,
    updated_at    timestamp,
    is_deleted    boolean,
    CONSTRAINT fk_user_id_for_category FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE posts
(
    post_id       bigint auto_increment primary key,
    user_id       bigint,
    category_id   bigint,
    selected_date varchar(255) NOT NULL,
    content       varchar(500) NOT NULL,
    score         integer      NOT NULL,
    file_url      varchar(255),
    created_at    timestamp,
    updated_at    timestamp,
    is_deleted    boolean,
    CONSTRAINT fk_user_id_for_post FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_category_id_for_post FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
