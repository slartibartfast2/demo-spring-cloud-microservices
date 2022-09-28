package ea.slartibartfast.gateway.server.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class HealthConfig {

    private WebClient webClient;

    private final WebClient.Builder webClientBuilder;

    @Bean
    public ReactiveHealthContributor coreServices() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();

        registry.put("payment-service", () -> getHealth("http://payment-api-service"));
        registry.put("merchant-service", () -> getHealth("http://merchant-api-service"));
        registry.put("card-service", () -> getHealth("http://card-api-service"));
        registry.put("auth-server", () -> getHealth("http://authorization-server"));

        return CompositeReactiveHealthContributor.fromMap(registry);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        log.debug("Will call the Health API on URL: {}", url);
        return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
                        .map(s -> new Health.Builder().up().build())
                        .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                        .log();
    }

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }
}
