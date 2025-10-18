-- USERS table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- TASK CATEGORIES table
CREATE TABLE IF NOT EXISTS task_categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- TASK STATUS table
CREATE TABLE IF NOT EXISTS task_status (
    id SERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL UNIQUE
);

-- TASK PRIORITY table
CREATE TABLE IF NOT EXISTS task_priority (
    id SERIAL PRIMARY KEY,
    priority VARCHAR(20) NOT NULL UNIQUE
);

-- TASKS table
CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    due_date DATE CHECK (due_date >= CURRENT_DATE),
    due_time TIMESTAMP,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status_id INTEGER NOT NULL REFERENCES task_status(id),
    priority_id INTEGER NOT NULL REFERENCES task_priority(id)
);

-- TASK CATEGORY MAPPING (many-to-many)
CREATE TABLE IF NOT EXISTS task_category_mapping (
    task_id INTEGER NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    category_id INTEGER NOT NULL REFERENCES task_categories(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, category_id)
);
