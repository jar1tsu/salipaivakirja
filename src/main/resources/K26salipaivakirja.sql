-- Poistetaan taulut oikeassa järjestyksessä riippuvuuksien takia
DROP TABLE IF EXISTS exercise_muscle_group;
DROP TABLE IF EXISTS exercise_set;
DROP TABLE IF EXISTS exercise;
DROP TABLE IF EXISTS workout;
DROP TABLE IF EXISTS muscle_groups;
DROP TABLE IF EXISTS users;

-- users-taulu
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(15) NOT NULL
);

-- muscle_groups-taulu
CREATE TABLE muscle_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- workout-taulu
CREATE TABLE workout (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    duration_min INTEGER,
    notes VARCHAR(250),
    user_id BIGINT REFERENCES users(id)
);

-- exercise-taulu
CREATE TABLE exercise (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30),
    workout_id BIGINT REFERENCES workout(id)
);

-- exercise_muscle_group-taulu
CREATE TABLE exercise_muscle_group (
    id BIGSERIAL PRIMARY KEY,
    exercise_id BIGINT REFERENCES exercise(id),
    muscle_group_id BIGINT REFERENCES muscle_groups(id)
);

-- exercise_set-taulu
CREATE TABLE exercise_set (
    id BIGSERIAL PRIMARY KEY,
    set_number INTEGER NOT NULL,
    weight FLOAT NOT NULL,
    reps INTEGER NOT NULL,
    completed BOOLEAN,
    exercise_id BIGINT REFERENCES exercise(id)
);

-- Testihaut
SELECT * FROM users;
SELECT * FROM muscle_groups;
