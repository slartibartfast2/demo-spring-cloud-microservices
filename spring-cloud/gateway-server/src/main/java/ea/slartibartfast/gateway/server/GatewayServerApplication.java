package ea.slartibartfast.gateway.server;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters, RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-service")).forEach(routeDefinition -> {
            String name = routeDefinition.getId().replaceAll("-api-service", "");
            swaggerUiConfigParameters.addGroup(name);
            GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
        });
        return groups;
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components())
                .info(new io.swagger.v3.oas.models.info.Info().title("Gateway API").version(appVersion)
                                                              .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
