package sk.eea.arttag.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import sk.eea.arttag.service.AccountConnectionSignUpService;
import sk.eea.arttag.service.SpringSecuritySignInAdapter;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AccountConnectionSignUpService accountConnectionSignUpService;


    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(
                        environment.getProperty("facebook.appId"),
                        environment.getProperty("facebook.appSecret")));
        connectionFactoryConfigurer.addConnectionFactory(new GoogleConnectionFactory(
                environment.getProperty("google.appId"),
                environment.getProperty("google.appSecret")));
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }



    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        ConnectController controller = new ConnectController(
                connectionFactoryLocator, connectionRepository);
        final String applicationURL =
                environment.getProperty("application.hostname-prefix") +
                        environment.getProperty("application.hostname") +
                        environment.getProperty("application.context-path");
        controller.setApplicationUrl(applicationURL);

        return controller;
    }

    @Bean
    public ProviderSignInController providerSignInController(
            ConnectionFactoryLocator connectionFactoryLocator,
            UsersConnectionRepository usersConnectionRepository,
            SpringSecuritySignInAdapter springSecuritySignInAdapter) {
        ProviderSignInController controller = new ProviderSignInController(
                connectionFactoryLocator,
                usersConnectionRepository,
                springSecuritySignInAdapter);

        final String applicationURL =
                environment.getProperty("application.hostname-prefix") +
                        environment.getProperty("application.hostname") +
                        environment.getProperty("application.context-path");
        controller.setApplicationUrl(applicationURL);
        return controller;
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setConnectionSignUp(accountConnectionSignUpService);

        return repository;
    }

}

