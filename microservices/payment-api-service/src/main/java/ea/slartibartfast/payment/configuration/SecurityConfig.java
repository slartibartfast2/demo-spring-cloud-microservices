package ea.slartibartfast.payment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/openapi/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers(POST, "/payment/**").hasAuthority("SCOPE_payment:write")
                .antMatchers(GET, "/payment/**").hasAuthority("SCOPE_payment:read")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}