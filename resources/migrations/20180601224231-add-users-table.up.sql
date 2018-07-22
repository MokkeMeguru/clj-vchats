CREATE TABLE USERS
(id SERIAL PRIMARY KEY,
user_name VARCHAR (30) NOT NULL UNIQUE,
password VARCHAR (30) NOT NULL,
mail VARCHAR (200) NOT NULL
);

CREATE TABLE CHANNELS
(chan_name VARCHAR (30) UNIQUE,
master_name VARCHAR (30) REFERENCES USERS (user_name),
inviter_name VARCHAR (30) REFERENCES USERS (user_name),
info VARCHAR (30),
inviter_face smallint DEFAULT 0,
inviter_bg smallint DEFAULT 0,
master_face smallint DEFAULT 0,
master_bg smallint DEFAULT 0,
PRIMARY KEY (chan_name, master_name)
);

CREATE TABLE MESSAGES
(id SERIAL PRIMARY KEY,
chan_name VARCHAR (30) NOT NULL REFERENCES CHANNELS (chan_name),
user_name VARCHAR (30) NOT NULL REFERENCES USERS (user_name),
messages VARCHAR(200) NOT NULL,
ltime timestamp
);

CREATE TABLE AUDIOS
(id BIGINT PRIMARY KEY,
chan_name VARCHAR (30) NOT NULL REFERENCES CHANNELS (chan_name),
audio bytea NOT NULL);
