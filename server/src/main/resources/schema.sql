DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Items CASCADE;
DROP TABLE IF EXISTS Booking CASCADE;
DROP TABLE IF EXISTS Comments CASCADE;
DROP TABLE IF EXISTS Requests CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uc_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(255)                            NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created      DATE                                    NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(255)                            NOT NULL,
    owner_id     BIGINT REFERENCES users (id) ON DELETE CASCADE,
    request_id   BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    is_available BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS booking
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_id    BIGINT REFERENCES items (id) ON DELETE CASCADE,
    booker_id    BIGINT REFERENCES users (id)   ON DELETE CASCADE,
    start_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    status     VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    author_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id   BIGINT REFERENCES items (id) ON DELETE CASCADE,
    text    VARCHAR(255)                            NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);
