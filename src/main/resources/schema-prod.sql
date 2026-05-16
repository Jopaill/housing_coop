CREATE TABLE IF NOT EXISTS users (
    username varchar(50)  NOT NULL PRIMARY KEY,
    password varchar(500) NOT NULL,
    enabled  boolean      NOT NULL
    );

CREATE TABLE IF NOT EXISTS authorities (
    username  varchar(50) NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users(username)
    );

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username, authority);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id         BIGSERIAL    PRIMARY KEY,
    token_hash varchar(255) NOT NULL UNIQUE,
    user_id    varchar(50)  NOT NULL,
    expires_at timestamp    NOT NULL,
    used       boolean      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_prt_users FOREIGN KEY (user_id) REFERENCES users(username)
);