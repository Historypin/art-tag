package sk.eea.arttag.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.social.security.SpringSocialConfigurer;
import sk.eea.arttag.ApplicationProperties;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select email as username, password, enabled from system_user where email = ? and identity_provider_type = 'LOCAL'")
                .authoritiesByUsernameQuery(
                        "select system_user.email as username, authorities.authority from system_user JOIN authorities ON system_user.id = authorities.user_id AND system_user.email = ? AND system_user.identity_provider_type = 'LOCAL'")
                .passwordEncoder(passwordEncoder());
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest().hasRole("API")
                    .and()
                    .httpBasic()
                    .and()
                    .csrf().disable();
        }
    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private ApplicationProperties applicationProperties;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/", "/register.do").permitAll()
                    .antMatchers("/js/**", "/css/**", "/webjars/**", "/fonts/**", "/img/**",
                            String.format("/%s/**", applicationProperties.getCulturalObjectsPublicPath())).permitAll()
                    .anyRequest().hasRole("USER")
                    .and()
                    .formLogin()
                    .loginPage("/#signinmodal")
                    .loginProcessingUrl("/login.do")
                    .defaultSuccessUrl("/join_page")
                    .failureUrl("/?loginError#signinmodal")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .and()
                    .apply(springSocialConfigurer())
                    .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/")
                    .maximumSessions(1);
        }

        @Bean(name = "userDetailsService")
        @Override
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }

        private SpringSocialConfigurer springSocialConfigurer() {
            SpringSocialConfigurer config = new SpringSocialConfigurer();
            config.postLoginUrl("/join_page");
            config.defaultFailureUrl("/failure");

            userDetailsService();

            return config;
        }
    }
}
