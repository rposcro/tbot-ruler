--liquibase formatted sql

--changeset initial:1

drop table if exists `plugins`;

create table `plugins`
(
    plugin_id       int             auto_increment,
    plugin_uuid     varchar(64)     not null unique,
    factory_class   varchar(256)    not null,
    name            varchar(64)     not null,
    configuration   json,
    version         int             not null default 0,

    primary key (plugin_id)
);

drop table if exists `things`;

create table `things`
(
    thing_id      int           auto_increment,
    thing_uuid    varchar(64)   not null unique,
    name          varchar(64)   not null,
    description   varchar(256),
    configuration json,
    version       int         not null default 0,

    primary key (thing_id)
);

drop table if exists `actuators`;

create table `actuators`
(
    actuator_id     int             auto_increment,
    actuator_uuid   varchar(64)     not null unique,
    thing_id        int             not null,
    plugin_id       int             not null,
    reference       varchar(32)     not null,
    name            varchar(64)     not null,
    description     varchar(256),
    configuration   json,
    version         int             not null default 0,

    primary key (actuator_id),
    foreign key (thing_id) references things (thing_id) on delete restrict,
    foreign key (plugin_id) references plugins (plugin_id) on delete restrict
);

drop table if exists `webhooks`;

create table `webhooks`
(
    webhook_id      int             auto_increment,
    webhook_uuid    varchar(64)     not null unique,
    owner           varchar(64)     not null,
    name            varchar(64)     not null,
    description     varchar(256),
    version         int             not null default 0,

    primary key (webhook_id)
);

drop table if exists `bindings`;

create table `bindings`
(
    sender_uuid     varchar(64)     not null,
    receiver_uuid   varchar(64)     not null,

    primary key (sender_uuid, receiver_uuid)
--     foreign key (sender_uuid) references actuators (actuator_uuid) on delete cascade,
--     foreign key (receiver_uuid) references actuators (actuator_uuid) on delete cascade
);

drop table if exists `subject_states`;

create table `subject_states`
(
    subject_uuid    varchar(64) not null unique,
    payload         json        not null,

    primary key (subject_uuid)
);

drop table if exists `stencils`;

create table `stencils`
(
    stencil_id       int             auto_increment,
    stencil_uuid     varchar(64)     not null unique,
    owner           varchar(64)     not null,
    type            varchar(64)     not null,
    payload         json            not null,
    version         int             not null default 0,

    primary key (stencil_id),
    constraint uq_stencil unique (owner, type)
);
