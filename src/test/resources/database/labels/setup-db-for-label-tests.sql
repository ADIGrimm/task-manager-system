INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES
    (1, 'user1@email.com', 'pass', 'First', 'User', false);

INSERT INTO projects (name, description, start_date, end_date, status, user_id)
VALUES
    ('Project 1', 'Project 1 desc', '2000-06-01', '2002-06-01', 'INITIATED', 1),
    ('Project 2', 'Project 2 desc', '2000-06-02', '2002-06-02', 'INITIATED', 1);

INSERT INTO tasks (name, description, priority, status, due_date, project_id, assignee_id)
VALUES
    ('Task 1', 'Task 1 desc', 'LOW', 'NOT_STARTED', '2000-06-01', 1, 1),
    ('Task 2', 'Task 2 desc', 'MEDIUM', 'IN_PROGRESS', '2000-06-02', 1, 1),
    ('Task 3', 'Task 3 desc', 'HIGH', 'COMPLETED', '2000-06-03', 2, 1);

