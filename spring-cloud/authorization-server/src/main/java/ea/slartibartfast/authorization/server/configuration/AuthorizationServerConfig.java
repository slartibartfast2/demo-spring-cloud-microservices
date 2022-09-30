package ea.slartibartfast.authorization.server.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.time.Duration;
import java.util.UUID;

import ea.slartibartfast.authorization.server.util.Jwks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    @Value("${app.jwt.issuer-uri}")
    private String jwtIssuerUri;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        // @formatter:off
        http
                .exceptionHandling(exceptions ->
                                           exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        // @formatter:on
        return http.build();
    }

    // @formatter:off
    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        log.info("register OAUth client allowing all grant flows...");
        RegisteredClient writerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                                                        .clientId("writer-client")
                                                        .clientSecret("{noop}writer-secret")
                                                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                                                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                                        .redirectUri("https://my.redirect.uri")
                                                        .redirectUri("https://localhost:8443/webjars/swagger-ui/oauth2-redirect.html")
                                                        .scope(OidcScopes.OPENID)
                                                        .scope("payment:read")
                                                        .scope("payment:write")
                                                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                                                        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(12)).build())
                                                        .build();

        RegisteredClient readerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                                                        .clientId("reader-client")
                                                        .clientSecret("{noop}reader-secret")
                                                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                                                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                                        .redirectUri("https://my.redirect.uri")
                                                        .redirectUri("https://localhost:8443/webjars/swagger-ui/oauth2-redirect.html")
                                                        .scope(OidcScopes.OPENID)
                                                        .scope("payment:read")
                                                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                                                        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(12)).build())
                                                        .build();
        return new InMemoryRegisteredClientRepository(writerClient, readerClient);
    }
    // @formatter:on

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                               .issuer(jwtIssuerUri)
                               .build();
    }

}
