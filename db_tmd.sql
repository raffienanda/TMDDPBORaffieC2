CREATE DATABASE IF NOT EXISTS db_tmd;
USE db_tmd;

CREATE TABLE tbenefit (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    skor INT DEFAULT 0,
    peluru_meleset INT DEFAULT 0,
    sisa_peluru INT DEFAULT 0
);