DROP SEQUENCE hibernate_sequence;
DROP CREATE TABLE cultural_object;
DROP CREATE TABLE cultural_object_description;
DROP TABLE cultural_object_tags;
DROP TABLE localized_string;
DROP TABLE system_user;
DROP TABLE tag;
DROP TABLE user_favourite;

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
    author character varying(255),
    external_id character varying(255),
    external_url character varying(255),
    image_path character varying(255)
);


ALTER TABLE cultural_object OWNER TO arttag;

--
-- TOC entry 174 (class 1259 OID 182428)
-- Name: cultural_object_description; Type: TABLE; Schema: public; Owner: arttag; Tablespace: 
--

CREATE TABLE cultural_object_description (
    cultural_object bigint NOT NULL,
    description bigint NOT NULL
);


ALTER TABLE cultural_object_description OWNER TO arttag;

--
-- TOC entry 175 (class 1259 OID 182431)
-- Name: cultural_object_tags; Type: TABLE; Schema: public; Owner: arttag; Tablespace: 
--

CREATE TABLE cultural_object_tags (
    cultural_object bigint NOT NULL,
    tags bigint NOT NULL
);


ALTER TABLE cultural_object_tags OWNER TO arttag;

--
-- TOC entry 176 (class 1259 OID 182434)
-- Name: localized_string; Type: TABLE; Schema: public; Owner: arttag; Tablespace: 
--

CREATE TABLE localized_string (
    id bigint NOT NULL,
    language character varying(3),
    value character varying(255)
);


ALTER TABLE localized_string OWNER TO arttag;

--
-- TOC entry 177 (class 1259 OID 182442)
-- Name: system_user; Type: TABLE; Schema: public; Owner: arttag; Tablespace: 
--

CREATE TABLE system_user (
    login character varying(255) NOT NULL,
    nick_name character varying(255),
    password character varying(255),
    games_played bigint,
    games_won bigint,
    total_score bigint
);


ALTER TABLE system_user OWNER TO arttag;

--
-- TOC entry 178 (class 1259 OID 182450)
-- Name: tag; Type: TABLE; Schema: public; Owner: arttag; Tablespace: 
--

CREATE TABLE tag (
    id bigint NOT NULL,
    language character varying(3),
    value character varying(255)
);


ALTER TABLE tag OWNER TO arttag;

--
-- TOC entry 179 (class 1259 OID 182458)
-- Name: user_favourite; Type: TABLE; Schema: public; Owner: arttag; Tablespace: 
--

CREATE TABLE user_favourite (
    system_user character varying(255) NOT NULL,
    favourite_objects bigint NOT NULL
);


ALTER TABLE user_favourite OWNER TO arttag;

--
-- TOC entry 1946 (class 2606 OID 182427)
-- Name: cultural_object_pkey; Type: CONSTRAINT; Schema: public; Owner: arttag; Tablespace: 
--

ALTER TABLE ONLY cultural_object
    ADD CONSTRAINT cultural_object_pkey PRIMARY KEY (id);


--
-- TOC entry 1952 (class 2606 OID 182441)
-- Name: localized_string_pkey; Type: CONSTRAINT; Schema: public; Owner: arttag; Tablespace: 
--

ALTER TABLE ONLY localized_string
    ADD CONSTRAINT localized_string_pkey PRIMARY KEY (id);


--
-- TOC entry 1954 (class 2606 OID 182449)
-- Name: system_user_pkey; Type: CONSTRAINT; Schema: public; Owner: arttag; Tablespace: 
--

ALTER TABLE ONLY system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (login);


--
-- TOC entry 1956 (class 2606 OID 182457)
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: arttag; Tablespace: 
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- TOC entry 1948 (class 2606 OID 182462)
-- Name: uk_h2i90e72pisp2vuj5utfldjlu; Type: CONSTRAINT; Schema: public; Owner: arttag; Tablespace: 
--

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT uk_h2i90e72pisp2vuj5utfldjlu UNIQUE (description);


--
-- TOC entry 1950 (class 2606 OID 182464)
-- Name: uk_reigdowq3dre8tbtqg7filwvm; Type: CONSTRAINT; Schema: public; Owner: arttag; Tablespace: 
--

ALTER TABLE ONLY cultural_object_tags
    ADD CONSTRAINT uk_reigdowq3dre8tbtqg7filwvm UNIQUE (tags);


--
-- TOC entry 1960 (class 2606 OID 182480)
-- Name: fk_424lgt1m9meggwuwsltdnexhd; Type: FK CONSTRAINT; Schema: public; Owner: arttag
--

ALTER TABLE ONLY cultural_object_tags
    ADD CONSTRAINT fk_424lgt1m9meggwuwsltdnexhd FOREIGN KEY (cultural_object) REFERENCES cultural_object(id);


--
-- TOC entry 1962 (class 2606 OID 182490)
-- Name: fk_8ljy7tssl4ahp47njfgyl25yr; Type: FK CONSTRAINT; Schema: public; Owner: arttag
--

ALTER TABLE ONLY user_favourite
    ADD CONSTRAINT fk_8ljy7tssl4ahp47njfgyl25yr FOREIGN KEY (system_user) REFERENCES system_user(login);


--
-- TOC entry 1961 (class 2606 OID 182485)
-- Name: fk_8xf40q0ykhcn5s9o9cex36viq; Type: FK CONSTRAINT; Schema: public; Owner: arttag
--

ALTER TABLE ONLY user_favourite
    ADD CONSTRAINT fk_8xf40q0ykhcn5s9o9cex36viq FOREIGN KEY (favourite_objects) REFERENCES cultural_object(id);


--
-- TOC entry 1958 (class 2606 OID 182470)
-- Name: fk_grueo06cbonwq7qyu62wd9q3a; Type: FK CONSTRAINT; Schema: public; Owner: arttag
--

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT fk_grueo06cbonwq7qyu62wd9q3a FOREIGN KEY (cultural_object) REFERENCES cultural_object(id);


--
-- TOC entry 1957 (class 2606 OID 182465)
-- Name: fk_h2i90e72pisp2vuj5utfldjlu; Type: FK CONSTRAINT; Schema: public; Owner: arttag
--

ALTER TABLE ONLY cultural_object_description
    ADD CONSTRAINT fk_h2i90e72pisp2vuj5utfldjlu FOREIGN KEY (description) REFERENCES localized_string(id);


--
-- TOC entry 1959 (class 2606 OID 182475)
-- Name: fk_reigdowq3dre8tbtqg7filwvm; Type: FK CONSTRAINT; Schema: public; Owner: arttag
--

ALTER TABLE ONLY cultural_object_tags
    ADD CONSTRAINT fk_reigdowq3dre8tbtqg7filwvm FOREIGN KEY (tags) REFERENCES tag(id);


