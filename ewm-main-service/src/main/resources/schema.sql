drop table if exists users cascade;
drop table if exists categories cascade;
drop table if exists locations cascade;
drop table if exists events cascade;
drop table if exists compilations cascade;
drop table if exists compilations_events cascade;


CREATE TABLE IF NOT EXISTS USERS
(
    ID
    BIGINT
    generated
    by
    default as
    identity
    primary
    key,
    EMAIL
    VARCHAR
(
    64
) not null,
    NAME VARCHAR
(
    64
) not null,
    CONSTRAINT un_user_email UNIQUE
(
    EMAIL
)
    );

create table if not exists categories
(
    ID
    BIGINT
    generated
    by
    default as
    identity
    primary
    key,
    NAME
    varchar
(
    255
) not null
    );

create table if not exists locations
(
    ID
    BIGINT
    generated
    by
    default as
    identity
    primary
    key,
    lat
    float
    not
    null,
    lon
    float
    not
    null
);



create table if not exists events
(
    ID
    BIGINT
    generated
    by
    default as
    identity
    primary
    key,
    annotation
    varchar
(
    500
),
    category_id bigint,
    confirmed_Requests bigint,
    description text,
    event_Date timestamp without time zone,
    created_On timestamp
                         without time zone,
    initiator_id bigint,
    location_id bigint,
    paid boolean,
    participant_Limit bigint,
    published_On timestamp
                         without time zone,
    request_Moderation boolean,
    state varchar
(
    120
),
    title varchar
(
    120
),
    views bigint,
    constraint EVENTS_CATEGORIES_ID_FK
    foreign key
(
    category_id
)
    references categories
(
    ID
)
                         on delete cascade,
    constraint EVENTS_LOCATIONS_ID_FK
    foreign key
(
    location_id
)
    references locations
(
    ID
)
                         on delete cascade,
    constraint EVENTS_USERS_ID_FK
    foreign key
(
    initiator_id
)
    references USERS
(
    ID
)
                         on delete cascade
    );


create table if not exists Requests
(
    ID
    BIGINT
    generated
    by
    default as
    identity
    primary
    key,
    created
    timestamp
    without
    time
    zone,
    event_id
    bigint
    not
    null,
    requester_id
    bigint
    not
    null,
    status
    varchar
(
    120
) not null,
    constraint REQUESTS_EVENTS_ID_FK
    foreign key
(
    event_id
)
    references EVENTS
(
    ID
) on delete cascade,
    constraint REQUESTS_USERS_ID_FK
    foreign key
(
    requester_id
)
    references USERS
(
    ID
)
  on delete cascade
    );


CREATE TABLE IF NOT EXISTS compilations
(
    id
    BIGINT
    generated
    by
    default as
    identity
    primary
    key,
    pinned
    BOOLEAN,
    title
    VARCHAR
(
    550
)
    );

CREATE TABLE IF NOT EXISTS compilations_events
(
    event_id
    BIGINT,
    compilation_id
    BIGINT,
    CONSTRAINT
    fk_to_compilations
    FOREIGN
    KEY
(
    compilation_id
) REFERENCES compilations
(
    id
),
    CONSTRAINT fk_to_events
    FOREIGN KEY
(
    event_id
) REFERENCES events
(
    id
)
    );