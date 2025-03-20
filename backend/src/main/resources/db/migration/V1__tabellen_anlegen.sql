-- Create Event table
CREATE TABLE event
(
    id           serial PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    zeit_punkt   TIMESTAMP,
    adresse      VARCHAR(255),
    link         VARCHAR(255),
    beschreibung TEXT,
    branche      VARCHAR(100)
);

-- Create Foerderung table
CREATE TABLE foerderung
(
    id            serial PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    beschreibung  TEXT,
    date          TIMESTAMP,
    branche       VARCHAR(100),
    link_website  VARCHAR(255),
    link_formular VARCHAR(255)
);

-- Create Person table
CREATE TABLE person
(
    id          serial PRIMARY KEY,
    category    VARCHAR(255),
    institution VARCHAR(255),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    focus_areas VARCHAR(255),
    contact     VARCHAR(255),
    website     VARCHAR(255)
);

-- Create User table
CREATE TABLE app_user
(
    id       serial primary key,
    username VARCHAR(255)
);

-- Create ChatHistory table
CREATE TABLE chat_history
(
    app_user     integer references app_user (id),
    app_user_key integer,
    id           uuid PRIMARY KEY,
    role         VARCHAR(50),
    content      TEXT
);

create table company_info
(
    app_user            integer references app_user (id),
    company_name        varchar(200),
    number_of_employees integer
);