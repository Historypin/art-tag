package sk.eea.arttag.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Component;
import sk.eea.arttag.model.IdentityProviderType;
import sk.eea.arttag.model.User;
import sk.eea.arttag.model.UserRole;
import sk.eea.arttag.repository.UserRepository;

import java.math.BigInteger;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
public class AccountConnectionSignUpService implements ConnectionSignUp {

    private static final Logger LOG = LoggerFactory.getLogger(AccountConnectionSignUpService.class);

    @Autowired
    private UserRepository userRepository;

    public String execute(Connection<?> connection) {
        UserProfile userProfile = connection.fetchUserProfile();
        ConnectionData connectionData = connection.createData();

        // validate required parameters
        if(isEmpty(userProfile.getEmail())) {
            throw new EmailNotProvidedException("Email is required during user signup.");
        }

        User user = new User();
        user.setIdentityProviderType(IdentityProviderType.valueOf(connectionData.getProviderId().toUpperCase()));
        user.setEmail(userProfile.getEmail());
        user.setPassword("");
        user.setNickName(connectionData.getDisplayName());
        user.setEnabled(true);
        user.getUserRole().add(new UserRole(user, "ROLE_USER"));
        user = userRepository.save(user);

        LOG.debug("Created user with id: {} and provider: {}.", userProfile.getId(), connectionData.getProviderId());

        return user.getId().toString();
    }
}
