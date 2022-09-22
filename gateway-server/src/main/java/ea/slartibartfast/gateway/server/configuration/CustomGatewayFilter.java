package ea.slartibartfast.gateway.server.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomGatewayFilter extends AbstractGatewayFilterFactory {

    private final AuthenticationFilter authenticationFilter;

    public CustomGatewayFilter(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return authenticationFilter;
    }
}
