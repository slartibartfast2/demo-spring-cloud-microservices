package ea.slartibartfast.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class FallbackController {

    @GetMapping("/card-fallback")
    Flux<String> getFallback() {
        return Flux.just("Service is unhealthy!");
    }
}