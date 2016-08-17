INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (1, 'LOCAL', 'admin@email.sk', 'admin', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" VALUES (nextval('hibernate_sequence'), 1, 'LOCAL', 'ROLE_ADMIN');
INSERT INTO "authorities" VALUES (nextval('hibernate_sequence'), 1, 'LOCAL', 'ROLE_USER');

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (2, 'LOCAL', 'api@email.sk', 'api', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" VALUES (nextval('hibernate_sequence'), 2, 'LOCAL', 'ROLE_API');

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (3, 'LOCAL', 'user1@email.sk', 'user 1', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" VALUES (nextval('hibernate_sequence'), 3, 'LOCAL', 'ROLE_USER');

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (4, 'LOCAL', 'user2@email.sk', 'user 2', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" VALUES (nextval('hibernate_sequence'), 4, 'LOCAL', 'ROLE_USER');

INSERT INTO "system_user"(id, identity_provider_type, email, nick_name, password, games_played, games_won, total_score, enabled) VALUES (5, 'LOCAL', 'user3@email.sk', 'user 3', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities" VALUES (nextval('hibernate_sequence'), 5, 'LOCAL', 'ROLE_USER');
