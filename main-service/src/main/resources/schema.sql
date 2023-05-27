create table if not exists category (
    id bigint generated by default as identity primary key,
    name varchar unique
);

create table if not exists user_account (
    id bigint generated by default as identity primary key,
    email varchar unique,
    name varchar
);

create table if not exists event (
    id bigint generated by default as identity primary key,
    title varchar,
    annotation varchar,
    description varchar,
    paid bool,
    request_moderation bool,
    category_id bigint references category,
    location_lat float,
    location_lon float,
    participant_limit int,
    created_on timestamp,
    event_date timestamp,
    published_on timestamp,
    initiator_id bigint references user_account,
    state varchar
);

create table if not exists event_request (
    id bigint generated by default as identity primary key,
    event_id bigint references event,
    requester_id bigint references user_account,
    status varchar,
    created timestamp
);

create table if not exists compilation (
    id bigint generated by default as identity primary key,
    title varchar,
    pinned bool
);

create table if not exists event_compilation (
    event_id bigint references event,
    compilation_id bigint references compilation,
    primary key (event_id, compilation_id)
);

create table if not exists comment (
    id bigint generated by default as identity primary key,
    text varchar,
    event_id int references event,
    author_id int references user_account,
    created timestamp
)
