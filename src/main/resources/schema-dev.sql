-- Taken from https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
create table if not exists users (
(
    username varchar_ignorecase(50) not null primary key,
    password varchar_ignorecase(500) not null,
    enabled  boolean not null
);

create table if not exists authorities
(
    username  varchar_ignorecase(50) not null,
    authority varchar_ignorecase(50) not null,
    constraint fk_authorities_users foreign key (username) references users (username)
);

create unique index ix_auth_username on authorities (username, authority);

CREATE TABLE if not exists password_reset_tokens
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    user_id    VARCHAR(50)  NOT NULL, -- matches 'username' in users table
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_prt_users FOREIGN KEY (user_id) REFERENCES users (username)
);