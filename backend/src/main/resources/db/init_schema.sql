INSERT INTO "system_user"(login, nick_name, password, games_played, games_won, total_score, enabled) VALUES ('admin', 'admin', '$2a$10$nkbmsSXt5bUv2EhOrRWkuORIpDvrEWOUq6jlq8n1DfaWu3SEq6Zoy', null, null, null, true);
INSERT INTO "authorities"VALUES (nextval('hibernate_sequence'), 'admin', 'ROLE_ADMIN');
