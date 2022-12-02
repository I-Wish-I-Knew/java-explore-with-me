CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name  VARCHAR (150) NOT NULL,
    email      VARCHAR (150) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uq_user_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS categories
(
    category_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR (200) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (category_id),
    CONSTRAINT uq_category_name UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned          BOOLEAN NOT NULL,
    title           VARCHAR (200) NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (compilation_id)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR (2000) NOT NULL,
    category_id        BIGINT NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description        VARCHAR (7000),
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lat                REAL NOT NULL,
    lon                REAL NOT NULL,
    paid               BOOLEAN NOT NULL,
    initiator_id       BIGINT NOT NULL,
    participant_limit  INT NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state              VARCHAR NOT NULL,
    title              VARCHAR (120) NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (event_id),
    CONSTRAINT annotation CHECK (LENGTH(annotation) >= 20),
    CONSTRAINT description CHECK (LENGTH(annotation) >= 20),
    CONSTRAINT fk_events_users FOREIGN KEY (initiator_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_events_categories FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS compiled_events
(
    compilation_id  BIGINT NOT NULL,
    event_id        BIGINT NOT NULL,
    CONSTRAINT pk_compiled_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compiled_events_compilations FOREIGN KEY (compilation_id)
               REFERENCES compilations (compilation_id) ON DELETE CASCADE,
    CONSTRAINT fk_compiled_events_events FOREIGN KEY (event_id) REFERENCES events (event_id)
    );

CREATE TABLE IF NOT EXISTS participation_requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR NOT NULL,
    CONSTRAINT pk_participation_requests PRIMARY KEY (request_id),
    CONSTRAINT fk_participation_requests_events FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE,
    CONSTRAINT fk_participation_requests_users FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

