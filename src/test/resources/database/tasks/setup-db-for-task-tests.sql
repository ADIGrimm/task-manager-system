INSERT INTO users (email, password, first_name, last_name, is_deleted)
VALUES
    ('user1@email.com', '4321', 'Test', 'User 1', FALSE),
    ('user2@email.com', '4321', 'Test', 'User 2', FALSE),
    ('user3@email.com', '4321', 'Test', 'User 3', FALSE),
    ('user4@email.com', '4321', 'Test', 'User 4', FALSE);

INSERT INTO projects (name, description, start_date, end_date, status, user_id)
VALUES
    ('Project 1', 'Project 1 desc', '2000-06-01', '2002-06-01', 'INITIATED', 1),
    ('Project 2', 'Project 2 desc', '2000-06-02', '2002-06-02', 'INITIATED', 2),
    ('Project 3', 'Project 3 desc', '2000-06-03', '2002-06-03', 'COMPLETED', 1);