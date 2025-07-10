INSERT INTO tasks (name, description, priority, status, due_date, project_id, assignee_id)
VALUES
    ('Task 1', 'Task 1 desc', 'LOW', 'NOT_STARTED', '2000-06-01', 1, 2),
    ('Task 2', 'Task 2 desc', 'MEDIUM', 'IN_PROGRESS', '2000-06-02', 1, 3),
    ('Task 3', 'Task 3 desc', 'HIGH', 'COMPLETED', '2000-06-03', 2, 4),
    ('Task 4', 'Task 4 desc', 'MEDIUM', 'COMPLETED', '2000-06-04', 3, 2),
    ('Task 5', 'Task 5 desc', 'HIGH', 'IN_PROGRESS', '2000-06-05', 3, 3);