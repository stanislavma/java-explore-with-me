CREATE SEQUENCE IF NOT EXISTS CATEGORY_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS category
(
    id   BIGINT DEFAULT NEXTVAL('CATEGORY_ID_SEQ') PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS APP_USER_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS app_user
(
    id    BIGINT DEFAULT NEXTVAL('APP_USER_ID_SEQ') PRIMARY KEY,
    name  TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS LOCATION_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS location
(
    id        BIGINT DEFAULT NEXTVAL('LOCATION_ID_SEQ') PRIMARY KEY,
    lat  DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS EVENT_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS event
(
    id                 BIGINT                                   DEFAULT NEXTVAL('EVENT_ID_SEQ') PRIMARY KEY,
    category_id        BIGINT REFERENCES category (id) NOT NULL,
    initiator_id       BIGINT REFERENCES app_user (id) NOT NULL,
    location_id        BIGINT REFERENCES location (id) NOT NULL,
    state              VARCHAR(50)                     NOT NULL,
    title              VARCHAR(120)                    NOT NULL,
    annotation         VARCHAR(2000)                   NOT NULL,
    description        VARCHAR(7000),
    event_date         TIMESTAMP                       NOT NULL,
    paid               BOOLEAN                         NOT NULL,
    participant_limit  INTEGER                         NOT NULL DEFAULT 0,
    request_moderation BOOLEAN                         NOT NULL DEFAULT TRUE,
    created_on         TIMESTAMP                       NOT NULL,
    published_on       TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS COMPILATION_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS compilation
(
    id     BIGINT                DEFAULT NEXTVAL('COMPILATION_ID_SEQ') PRIMARY KEY,
    title  VARCHAR(255) NOT NULL,
    pinned BOOLEAN      NOT NULL default false
);

CREATE SEQUENCE IF NOT EXISTS COMPILATION_EVENT_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT REFERENCES compilation (id),
    event_id       BIGINT REFERENCES event (id),
    PRIMARY KEY (compilation_id, event_id)
);


CREATE SEQUENCE IF NOT EXISTS PARTICIPATION_REQUEST_ID_SEQ START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS participation_request
(
    id           BIGINT DEFAULT NEXTVAL('PARTICIPATION_REQUEST_ID_SEQ') PRIMARY KEY,
    event_id     BIGINT REFERENCES event (id)    NOT NULL,
    requester_id BIGINT REFERENCES app_user (id) NOT NULL,
    created      TIMESTAMP                       NOT NULL,
    status       VARCHAR(50)                     NOT NULL
);
