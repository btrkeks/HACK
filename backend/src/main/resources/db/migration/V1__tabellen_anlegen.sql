-- Create Event table
CREATE TABLE event (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       zeit_punkt TIMESTAMP,
                       adresse VARCHAR(255),
                       link VARCHAR(255),
                       beschreibung TEXT,
                       branche VARCHAR(100)
);

-- Create Foerderung table
CREATE TABLE foerderung (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            beschreibung TEXT,
                            date TIMESTAMP,
                            branche VARCHAR(100),
                            link_website VARCHAR(255),
                            link_formular VARCHAR(255)
);

-- Create Person table
CREATE TABLE person (
                        id uuid PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

-- Create User table
CREATE TABLE app_user (
                       id serial primary key,
                       username VARCHAR(255)
);

-- Create ChatHistory table
CREATE TABLE chat_history (
                              app_user integer references app_user (id),
                              app_user_key integer,
                              id uuid PRIMARY KEY,
                              role VARCHAR(50),
                              content TEXT
);