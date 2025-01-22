-- Удаляем существующие таблицы, если они есть
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friend_request CASCADE;

-- Создаем таблицу пользователей
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255),
  login VARCHAR(50),
  name VARCHAR(100),
  birthday DATE
);

-- Создаем таблицу фильмов
CREATE TABLE IF NOT EXISTS films (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  description VARCHAR(500),
  release_date DATE,
  duration INT,
  mpa_id INT
);

-- Создаем таблицу лайков
CREATE TABLE IF NOT EXISTS likes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  film_id INT,
  user_id INT
);

-- Создаем таблицу жанров
CREATE TABLE IF NOT EXISTS genre (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50)
);

-- Создаем таблицу связи фильм-жанр
CREATE TABLE IF NOT EXISTS film_genre (
  id INT AUTO_INCREMENT PRIMARY KEY,
  film_id INT,
  genre_id INT
);

-- Создаем таблицу возрастных рейтингов
CREATE TABLE IF NOT EXISTS mpa (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(10)
);

-- Создаем таблицу запросов дружбы
CREATE TABLE IF NOT EXISTS friend_request (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  friend_id INT,
  is_confirmed BOOLEAN
);

-- Добавляем внешние ключи
ALTER TABLE likes ADD CONSTRAINT fk_likes_film_id FOREIGN KEY (film_id) REFERENCES films(id);
ALTER TABLE likes ADD CONSTRAINT fk_likes_user_id FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE film_genre ADD CONSTRAINT fk_film_genre_film_id FOREIGN KEY (film_id) REFERENCES films(id);
ALTER TABLE film_genre ADD CONSTRAINT fk_film_genre_genre_id FOREIGN KEY (genre_id) REFERENCES genre(id);

ALTER TABLE films ADD CONSTRAINT fk_films_mpa_id FOREIGN KEY (mpa_id) REFERENCES mpa(id);

ALTER TABLE friend_request ADD CONSTRAINT fk_friend_request_user_id FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE friend_request ADD CONSTRAINT fk_friend_request_friend_id FOREIGN KEY (friend_id) REFERENCES users(id);
