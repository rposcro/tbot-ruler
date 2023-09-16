--liquibase formatted sql

--changeset initial:1

drop table if exists `plugins`;

create table `plugins`
(
    plugin_id       long            auto_increment,
    plugin_uuid     varchar(64)     not null,
    plugin_class    varchar(256)    not null,
    name            varchar(64)     not null,
    configuration   json,
    primary key (plugin_id)
);

drop table if exists `things`;

create table `things`
(
    thing_id        long        auto_increment,
    thing_uuid      varchar(64) not null unique,
    plugin_uuid     varchar(64) not null,
    name            varchar(64) not null,
    description     varchar(256),
    configuration   json,

    primary key (thing_id),
    foreign key plugin_uuid references plugins (plugin_uuid)
);

drop table if exists `actuators`;

create table `actuators`
(
    actuator_id     long            auto_increment,
    actuator_uuid   varchar(64)     not_null unique,
    thing_uuid      varchar(64)     not null,
    reference       varchar(32)     not null,
    name            varchar(64)     not null,
    description     varchar(256),
    configuration   json,

    primary key (actuator_id),
    foreign_key thing_uuid references things (thing_uuid)
);

drop table if exists `appliances`;

create table `appliances`
(
    appliance_id    long            auto_increment,
    appliance_uuid  varchar(64)     not_null unique,
    appliance_type  varchar(64)     not null,
    name            varchar(64)     not null,
    description     varchar(256),

    primary key (appliance_id)
);

drop table if exists `bindings`;

create table `bindings`
(
    sender_uuid     varchar(64)     not null,
    receiver_uuid   varchar(64)     not null
);
