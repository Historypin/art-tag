CREATE TABLE cultural_object (
    id bigint NOT NULL,
    active boolean,
    author character varying(255),
    batch_id character varying(255),
    external_id character varying(255),
    external_url character varying(255),
    image_path character varying(255)
);


ALTER TABLE cultural_object OWNER TO arttag;

CREATE TABLE cultural_object_description (
    cultural_object bigint NOT NULL,
    description bigint NOT NULL
);


ALTER TABLE cultural_object_description OWNER TO arttag;

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO arttag;

CREATE TABLE localized_string (
    id bigint NOT NULL,
    language character varying(255),
    value character varying(255)
);


ALTER TABLE localized_string OWNER TO arttag;

CREATE TABLE system_user (
    login character varying(255) NOT NULL,
    nick_name character varying(255),
    password character varying(255),
    games_played bigint,
    games_won bigint,
    total_score bigint
);


ALTER TABLE system_user OWNER TO arttag;


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


ALTER TABLE user_favourite OWNER TO arttag;


ALTER TABLE ONLY cultural_object
    ADD CONSTRAINT cultural_object_pkey PRIMARY KEY (id);


ALTER TABLE ONLY localized_string
    ADD CONSTRAINT localized_string_pkey PRIMARY KEY (id);


ALTER TABLE ONLY system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (login);

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT uk_h2i90e72pisp2vuj5utfldjlu UNIQUE (description);

ALTER TABLE ONLY tag
    ADD CONSTRAINT fk_1iwtqrxth63w8e7apvb6ixoeb FOREIGN KEY (co_id) REFERENCES cultural_object(id);

ALTER TABLE ONLY user_favourite
    ADD CONSTRAINT fk_8ljy7tssl4ahp47njfgyl25yr FOREIGN KEY (system_user) REFERENCES system_user(login);

ALTER TABLE ONLY user_favourite
    ADD CONSTRAINT fk_8xf40q0ykhcn5s9o9cex36viq FOREIGN KEY (favourite_objects) REFERENCES cultural_object(id);

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT fk_grueo06cbonwq7qyu62wd9q3a FOREIGN KEY (cultural_object) REFERENCES cultural_object(id);

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT fk_h2i90e72pisp2vuj5utfldjlu FOREIGN KEY (description) REFERENCES localized_string(id);

ALTER TABLE ONLY tag
    ADD CONSTRAINT fk_rr0jdrb0km505m2m59c7kjlwa FOREIGN KEY (value) REFERENCES localized_string(id);

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
