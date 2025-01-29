-- Удаляем существующие таблицы, если они есть
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS director CASCADE;
DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friend_request CASCADE;

-- Создаем таблицу пользователей
CREATE TABLE IF NOT EXISTS users
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(255),
    login    VARCHAR(50),
    name     VARCHAR(100),
    birthday DATE
);

-- Создаем уникальные индексы для полей email и login
CREATE UNIQUE INDEX IF NOT EXISTS USER_EMAIL_UINDEX ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS USER_LOGIN_UINDEX ON users (login);

-- Создаем таблицу жанров
CREATE TABLE IF NOT EXISTS genre
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);

-- Создаем таблицу возрастных рейтингов
CREATE TABLE IF NOT EXISTS mpa
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10)
);

-- Создаем таблицу фильмов
CREATE TABLE IF NOT EXISTS films
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    description  VARCHAR(500),
    release_date DATE,
    duration     INT,
    mpa_id       INTEGER REFERENCES mpa (id)
);


-- Создаем таблицу связи фильм-жанр
CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES films (id),
    genre_id INTEGER REFERENCES genre (id),
    PRIMARY KEY (film_id, genre_id)
);

-- Создаем таблицу с ФИО режиссера
CREATE TABLE IF NOT EXISTS director
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);


-- Создаем таблицу для связи режиссеров с фильмами
CREATE TABLE IF NOT EXISTS film_directors
(
    film_id     INTEGER REFERENCES films (id),
    director_id INTEGER REFERENCES director (id),
    PRIMARY KEY (film_id, director_id)
);

-- Создаем таблицу запросов дружбы
CREATE TABLE IF NOT EXISTS friend_request
(
    user_id      INTEGER REFERENCES users (id),
    friend_id    INTEGER REFERENCES users (id),
    is_confirmed BOOLEAN,
    PRIMARY KEY (user_id, friend_id)
);

-- Создаем таблицу лайков
CREATE TABLE IF NOT EXISTS likes
(
    user_id INTEGER REFERENCES users (id),
    film_id INTEGER REFERENCES films (id),
    PRIMARY KEY (user_id, film_id)
);


-- Добавляем внешние ключи
ALTER TABLE likes
    ADD CONSTRAINT IF NOT EXISTS fk_likes_film_id FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE likes
    ADD CONSTRAINT IF NOT EXISTS fk_likes_user_id FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE film_genre
    ADD CONSTRAINT IF NOT EXISTS fk_film_genre_film_id FOREIGN KEY (film_id) REFERENCES films (id);
ALTER TABLE film_genre
    ADD CONSTRAINT IF NOT EXISTS fk_film_genre_genre_id FOREIGN KEY (genre_id) REFERENCES genre (id);

ALTER TABLE films
    ADD CONSTRAINT IF NOT EXISTS fk_films_mpa_id FOREIGN KEY (mpa_id) REFERENCES mpa (id);

ALTER TABLE friend_request
    ADD CONSTRAINT IF NOT EXISTS fk_friend_request_user_id FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE friend_request
    ADD CONSTRAINT IF NOT EXISTS fk_friend_request_friend_id FOREIGN KEY (friend_id) REFERENCES users (id);