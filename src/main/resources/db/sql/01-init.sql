--liquibase formatted sql
--changeset zatsepina_viktoria:1

--- drop tables and type ---

DROP TABLE IF EXISTS products_history;
DROP  TABLE IF EXISTS products;
DROP TYPE IF EXISTS product_types;

--- CREATE tables and type ---
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TYPE product_types AS ENUM ('OFFER', 'CATEGORY');

CREATE TABLE products
(
    id  uuid UNIQUE,
    name varchar(64) NOT NULL,
    date timestamp NOT NULL,
    parent_id uuid,
    type varchar(64),
    price bigint ,
    remote boolean DEFAULT FALSE,


    CONSTRAINT pk_products_id PRIMARY KEY(id),
    CONSTRAINT fk_products_parent_id_id FOREIGN KEY (parent_id) REFERENCES products(id)

);

CREATE TABLE products_update_history
(
    id bigserial PRIMARY KEY,
    uuid  uuid NOT NULL,
    name varchar(64) NOT NULL,
    date timestamp NOT NULL,
    parent_id uuid,
    type varchar(64),
    price bigint,
    remote boolean DEFAULT FALSE,

    CONSTRAINT fk_products_history_id_id FOREIGN KEY (uuid) REFERENCES products(id)
);

