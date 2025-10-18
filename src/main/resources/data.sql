-- Insert predefined categories
INSERT INTO task_categories (name, description) VALUES
    ('Work', 'Tasks related to work'),
    ('Personal', 'Personal tasks and errands'),
    ('Fitness', 'Fitness and health-related tasks'),
    ('Hobbies', 'Tasks related to hobbies and leisure activities')
ON CONFLICT (name) DO NOTHING;

-- Insert predefined statuses
INSERT INTO task_status (status) VALUES
    ('In progress'),
    ('Completed')
ON CONFLICT (status) DO NOTHING;

-- Insert predefined priorities
INSERT INTO task_priority (priority) VALUES
    ('Low'),
    ('Medium'),
    ('High')
ON CONFLICT (priority) DO NOTHING;
