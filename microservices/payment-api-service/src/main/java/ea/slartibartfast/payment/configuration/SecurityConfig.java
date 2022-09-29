package ea.slartibartfast.payment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.mvcMatcher("/payment/**")
            .authorizeRequests()
            .mvcMatchers("/payment/**")
            .access("hasAuthority('SCOPE_api.read')")
            .and()
            .oauth2ResourceServer()
            .jwt();

        return http.build();
    }
}
