DELETE FROM projects;

ALTER TABLE projects ALTER COLUMN id RESTART WITH 1;
