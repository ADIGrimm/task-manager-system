INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES
    (1, 'user1@email.com', '4321', 'Test', 'User 1', FALSE),
    (2, 'user2@email.com', '4321', 'Test', 'User 2', FALSE),
    (3, 'user3@email.com', '4321', 'Test', 'User 3', FALSE);

INSERT INTO projects (name, description, start_date, end_date, status, user_id)
VALUES
    ('Project 1', 'Project 1 desc', '2000-06-01', '2002-06-01', 'INITIATED', 1);

INSERT INTO tasks (name, description, priority, status, due_date, project_id, assignee_id)
VALUES
    ('Task 1', 'Task 1 desc', 'LOW', 'NOT_STARTED', '2000-06-01', 1, 2),
    ('Task 2', 'Task 2 desc', 'MEDIUM', 'IN_PROGRESS', '2000-06-02', 1, 3);