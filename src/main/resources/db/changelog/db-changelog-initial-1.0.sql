--liquibase formatted sql

--changeset initial:1

drop table if exists `plugins`;

create table `plugins`
(
    plugin_id       long            auto_increment,
    plugin_uuid     varchar(64)     not null unique,
    plugin_class    varchar(256)    not null,
    name            varchar(64)     not null,
    configuration   json,

    primary key (plugin_id)
);

drop table if exists `things`;

create table `things`
(
    thing_id      long        auto_increment,
    thing_uuid    varchar(64) not null unique,
    plugin_id     long        not null,
    name          varchar(64) not null,
    description   varchar(256),
    configuration json,

    primary key (thing_id),
    foreign key (plugin_id) references plugins (plugin_id) on delete restrict
);

drop table if exists `actuators`;

create table `actuators`
(
    actuator_id     long            auto_increment,
    actuator_uuid   varchar(64)     not null unique,
    thing_id        long            not null,
    reference       varchar(32)     not null,
    name            varchar(64)     not null,
    description     varchar(256),
    configuration   json,

    primary key (actuator_id),
    foreign key (thing_id) references things (thing_id) on delete restrict
);

drop table if exists `bindings`;

create table `bindings`
(
    sender_id     long     not null,
    receiver_id   long     not null,

    primary key (sender_id, receiver_id),
    foreign key (sender_id) references actuators (actuator_id) on delete cascade,
    foreign key (receiver_id) references actuators (actuator_id) on delete cascade
);

drop table if exists `subject_states`;

create table `subject_states`
(
    subject_uuid    varchar(64) not null unique,
    payload         json        not null
)
