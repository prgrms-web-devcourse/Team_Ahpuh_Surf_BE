INSERT IGNORE INTO `categories` (`category_id`, `user_id`, `name`, `is_public`, `color_code`, `created_at`, `updated_at`, `is_deleted`)
VALUES
    (1, 2, 'category1', 1, '#000000', '2021-12-12 14:15:15', '2021-12-12 14:15:15', 0),
    (2, 3, 'category2', 1, '#000000', '2021-12-12 14:15:30', '2021-12-12 14:15:30', 0),
    (3, 4, 'category4', 1, '#000000', '2021-12-12 14:33:45', '2021-12-12 14:33:45', 0),
    (4, 1, 'category', 1, '#000000', '2021-12-15 09:01:33', '2021-12-15 09:01:33', 0),
    (5, 1, 'categorytest', 1, '#000000', '2021-12-15 09:18:13', '2021-12-15 09:18:13', 0),
    (6, 7, 'Vue 😎', 1, '#d0ebff', '2021-12-15 09:22:45', '2021-12-21 10:51:40', 0),
    (7, 7, 'React', 1, '#74c0fc', '2021-12-15 09:25:28', '2021-12-18 13:08:08', 0),
    (8, 7, 'Spring', 1, '#339af0', '2021-12-15 09:26:29', '2021-12-18 13:08:25', 0),
    (9, 7, 'AngularJS', 1, '#1c73d6', '2021-12-15 09:27:32', '2021-12-18 13:08:43', 0),
    (10, 7, 'JavaScript', 1, '#1864ab', '2021-12-15 09:28:14', '2021-12-15 09:28:14', 0),
    (11, 2, 'categorytest11', 1, '#000000', '2021-12-15 14:46:43', '2021-12-15 14:46:43', 0),
    (12, 1, 'category', 1, '#111111', '2021-12-15 15:20:15', '2021-12-15 15:20:15', 0),
    (13, 1, 'category', 1, '#111111', '2021-12-15 15:20:21', '2021-12-15 15:20:21', 0),
    (14, 1, 'category😃', 0, '#111112', '2021-12-15 16:09:38', '2021-12-15 16:20:36', 0),
    (15, 7, 'test category', 1, '#000000', '2021-12-16 06:16:11', '2021-12-18 14:21:18', 0);