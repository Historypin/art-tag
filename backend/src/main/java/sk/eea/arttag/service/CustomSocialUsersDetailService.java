package sk.eea.arttag.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.eea.arttag.model.User;
import sk.eea.arttag.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class CustomSocialUsersDetailService implements SocialUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSocialUsersDetailService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        final User user = userRepository.findOne(Long.parseLong(userId));
        if (user == null) {
            throw new UsernameNotFoundException("User with ID: " + userId + " could not be found!");
        }

        final List<GrantedAuthority> authorities = user.getUserRole().stream().map(
                userRole -> new SimpleGrantedAuthority(userRole.getRole())
        ).collect(Collectors.toList());

        return new SocialUser(user.getId().toString(), user.getPassword(), authorities);
    }
}
