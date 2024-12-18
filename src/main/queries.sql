-- -- Query to view all users
-- SELECT
--     id,
--     username,
--     password,
--     email,
--     created_at
-- FROM
--     users;


-- -- Query to delete all users
-- DELETE FROM users;


-- -- Query to view all tasks
-- SELECT
--     t.id AS task_id,
--     t.title,
--     t.description,
--     t.due_date,
--     u.username AS assigned_to,
--     c.name AS category,
--     s.status AS task_status,
--     p.priority AS task_priority
-- FROM
--     tasks t
-- JOIN
--     users u ON t.user_id = u.id
-- LEFT JOIN
--     categories c ON t.category_id = c.id
-- JOIN
--     task_status s ON t.status_id = s.id
-- JOIN
--     task_priority p ON t.priority_id = p.id;


-- -- Query to delete all tasks
-- DELETE FROM tasks;
