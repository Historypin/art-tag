INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (nextval('hibernate_sequence'), 'LOCAL', 'admin@email.sk', 'admin', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" SELECT nextval('hibernate_sequence'), id, 'ROLE_USER' FROM "system_user" WHERE email = 'admin@email.sk';

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (nextval('hibernate_sequence'), 'LOCAL', 'api@email.sk', 'api', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" SELECT nextval('hibernate_sequence'), id, 'ROLE_API' FROM "system_user" WHERE email = 'api@email.sk';

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (nextval('hibernate_sequence'), 'LOCAL', 'user1@email.sk', 'user 1', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" SELECT nextval('hibernate_sequence'), id, 'ROLE_USER' FROM "system_user" WHERE email = 'user1@email.sk';

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (nextval('hibernate_sequence'), 'LOCAL', 'user2@email.sk', 'user 2', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" SELECT nextval('hibernate_sequence'), id, 'ROLE_USER' FROM "system_user" WHERE email = 'user2@email.sk';

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (nextval('hibernate_sequence'), 'LOCAL', 'user3@email.sk', 'user 3', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" SELECT nextval('hibernate_sequence'), id, 'ROLE_USER' FROM "system_user" WHERE email = 'user3@email.sk';
