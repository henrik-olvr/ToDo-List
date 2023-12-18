--CREATE DATABASE todolist;

\c todolist;

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    owner VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    currentstatus VARCHAR(4) CHECK(currentstatus = 'ToDo' OR currentstatus = 'Done') NOT NULL
);