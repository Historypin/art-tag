DROP SEQUENCE IF EXISTS hibernate_sequence CASCADE;
DROP TABLE IF EXISTS cultural_object CASCADE;
DROP TABLE IF EXISTS cultural_object_description CASCADE;
DROP TABLE IF EXISTS cultural_object_tags CASCADE;
DROP TABLE IF EXISTS localized_string CASCADE;
DROP TABLE IF EXISTS system_user CASCADE;
DROP TABLE IF EXISTS tag CASCADE;
DROP TABLE IF EXISTS user_favourite CASCADE;
DROP TABLE IF EXISTS authorities CASCADE;
DROP INDEX IF EXISTS ix_auth_login CASCADE;
DROP INDEX IF EXISTS ix_batch_id CASCADE;

CREATE SEQUENCE hibernate_sequence
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER TABLE hibernate_sequence
    OWNER TO arttag;

CREATE TABLE cultural_object (
    id bigint NOT NULL,
    active boolean,
    author character varying(255),
    batch_id bigint NOT NULL,
    external_id character varying(255),
    external_url text,
    external_source text,
    internal_file_system_path character varying(255),
    public_source text,
    last_selected timestamp without time zone,
    number_of_selections integer DEFAULT 0
);

ALTER TABLE cultural_object OWNER TO arttag;

CREATE TABLE cultural_object_description (
    cultural_object bigint NOT NULL,
    description bigint NOT NULL
);


ALTER TABLE cultural_object_description OWNER TO arttag;

CREATE TABLE localized_string (
    id bigint NOT NULL,
    language character varying(255),
    value character varying(255)
);


ALTER TABLE localized_string OWNER TO arttag;

CREATE TABLE system_user (
    login character varying(255) PRIMARY KEY NOT NULL,
    nick_name character varying(255),
    password character varying(255),
    games_played bigint,
    games_won bigint,
    total_score bigint,
    enabled boolean NOT NULL
);


ALTER TABLE system_user OWNER TO arttag;

CREATE TABLE authorities (
    id bigint PRIMARY KEY NOT NULL,
    login character varying(255) NOT NULL,
    authority character varying(255) NOT NULL,
    CONSTRAINT fk_authorities_users foreign key(login) references system_user(login) ON DELETE CASCADE
);

ALTER TABLE authorities OWNER TO arttag;

CREATE UNIQUE INDEX ix_auth_login on authorities (login,authority);

CREATE TABLE tag (
    id bigint NOT NULL,
    created timestamp without time zone,
    co_id bigint,
    hit_score bigint,
    value bigint
);


ALTER TABLE tag OWNER TO arttag;

CREATE TABLE user_favourite (
    system_user character varying(255) NOT NULL,
    favourite_objects bigint NOT NULL
);

CREATE INDEX ix_batch_id ON cultural_object(batch_id);

ALTER TABLE user_favourite OWNER TO arttag;

ALTER TABLE ONLY cultural_object
    ADD CONSTRAINT cultural_object_pkey PRIMARY KEY (id);

ALTER TABLE ONLY localized_string
    ADD CONSTRAINT localized_string_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT uk_h2i90e72pisp2vuj5utfldjlu UNIQUE (description);

ALTER TABLE ONLY tag
    ADD CONSTRAINT fk_1iwtqrxth63w8e7apvb6ixoeb FOREIGN KEY (co_id) REFERENCES cultural_object(id) ON DELETE CASCADE;

ALTER TABLE ONLY user_favourite
    ADD CONSTRAINT fk_8ljy7tssl4ahp47njfgyl25yr FOREIGN KEY (system_user) REFERENCES system_user(login) ON DELETE CASCADE;

ALTER TABLE ONLY user_favourite
    ADD CONSTRAINT fk_8xf40q0ykhcn5s9o9cex36viq FOREIGN KEY (favourite_objects) REFERENCES cultural_object(id) ON DELETE CASCADE;

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT fk_grueo06cbonwq7qyu62wd9q3a FOREIGN KEY (cultural_object) REFERENCES cultural_object(id) ON DELETE CASCADE;

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT fk_h2i90e72pisp2vuj5utfldjlu FOREIGN KEY (description) REFERENCES localized_string(id) ON DELETE CASCADE;

ALTER TABLE ONLY tag
    ADD CONSTRAINT fk_rr0jdrb0km505m2m59c7kjlwa FOREIGN KEY (value) REFERENCES localized_string(id) ON DELETE CASCADE;
