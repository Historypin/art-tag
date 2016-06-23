INSERT INTO "system_user"(login, nick_name, password, games_played, games_won, total_score, enabled) VALUES ('admin', 'admin', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities"VALUES (nextval('hibernate_sequence'), 'admin', 'ROLE_ADMIN');

INSERT INTO "system_user"(login, nick_name, password, games_played, games_won, total_score, enabled) VALUES ('user1', 'user 1', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities"VALUES (nextval('hibernate_sequence'), 'user1', 'ROLE_ADMIN');

INSERT INTO "system_user"(login, nick_name, password, games_played, games_won, total_score, enabled) VALUES ('user2', 'user 2', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities"VALUES (nextval('hibernate_sequence'), 'user2', 'ROLE_ADMIN');

INSERT INTO "system_user"(login, nick_name, password, games_played, games_won, total_score, enabled) VALUES ('user3', 'user 3', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities"VALUES (nextval('hibernate_sequence'), 'user3', 'ROLE_ADMIN');
