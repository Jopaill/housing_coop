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
    username    varchar(50)  NOT NULL,
    expires_at timestamp    NOT NULL,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used       boolean      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_prt_users FOREIGN KEY (username) REFERENCES users(username)
);


CREATE TABLE IF NOT EXISTS persons (
    person_id    BIGSERIAL    PRIMARY KEY,
    username      varchar(50),
    first_name   varchar(50)  NOT NULL,
    last_name    varchar(50)  NOT NULL,
    phone        varchar(18),
    is_member     boolean      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_person_users FOREIGN KEY (username) REFERENCES users(username)
);
CREATE UNIQUE INDEX IF NOT EXISTS person_name_unique ON persons (first_name, last_name);



CREATE TABLE IF NOT EXISTS apartments (
    apartment_id   BIGSERIAL    PRIMARY KEY,
    civic_number   varchar(8)   NOT NULL
);

CREATE TABLE IF NOT EXISTS person_apartment (
    person_id     BIGINT       NOT NULL,
    apartment_id  BIGINT       NOT NULL,
    CONSTRAINT fk_pap_person_id FOREIGN KEY (person_id) REFERENCES persons(person_id),
    CONSTRAINT fk_pap_apartment_id FOREIGN KEY (apartment_id) REFERENCES apartments(apartment_id),
    PRIMARY KEY (person_id, apartment_id)
);





CREATE TABLE IF NOT EXISTS admins (
    admin_id     BIGSERIAL       PRIMARY KEY,
    admin_name   varchar(15)     NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS admins_unique ON admins (admin_name);


CREATE TABLE IF NOT EXISTS admin_person (
    admin_id     BIGINT     NOT NULL,
    person_id    BIGINT     NOT NULL,
    CONSTRAINT fk_ap_admin_id FOREIGN KEY (admin_id) REFERENCES admins(admin_id),
    CONSTRAINT fk_ap_person_id FOREIGN KEY (person_id) REFERENCES persons(person_id),
    PRIMARY KEY (admin_id, person_id)
);





CREATE TABLE IF NOT EXISTS committees (
    committee_id       BIGSERIAL    PRIMARY KEY,
    committee_name     varchar(50)  NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS committees_unique ON committees (committee_name);

CREATE TABLE IF NOT EXISTS person_committee (
    person_id           BIGINT NOT NULL,
    person_committee_id BIGINT NOT NULL,
    CONSTRAINT fk_pc_person_id FOREIGN KEY (person_id) REFERENCES persons(person_id),
    CONSTRAINT fk_pc_committee_id FOREIGN KEY (committee_id) REFERENCES committees(committee_id),
    PRIMARY KEY (person_id, person_committee_id)
);


CREATE TABLE IF NOT EXISTS responsibilities (
    responsibility_id    BIGSERIAL    PRIMARY KEY,
    responsibility_name  varchar(50)   NOT NULL,
    committee_id         BIGINT,
    CONSTRAINT fk_r_committee_id FOREIGN KEY (committee_id) REFERENCES committees(committee_id)
);
CREATE UNIQUE INDEX IF NOT EXISTS responsibilities_unique ON responsibilities (responsibility_name);


CREATE TABLE IF NOT EXISTS person_responsibility (
    person_id            BIGINT     NOT NULL,
    responsibility_id    BIGINT     NOT NULL,
    CONSTRAINT fk_pr_person_id FOREIGN KEY (person_id) REFERENCES persons(person_id),
    CONSTRAINT fk_pr_responsibility_id FOREIGN KEY (responsibility_id) REFERENCES responsibilities(responsibility_id),
    PRIMARY KEY (person_id, responsibility_id)
);

