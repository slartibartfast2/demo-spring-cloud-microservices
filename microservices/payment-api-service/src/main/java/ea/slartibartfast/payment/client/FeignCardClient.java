package ea.slartibartfast.payment.client;

import ea.slartibartfast.payment.model.Card;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "card-api-service")
@ConditionalOnProperty(value = "mock.card.client.enabled", havingValue = "false", matchIfMissing = true)
public interface FeignCardClient extends CardClient {

    @Retry(name = "cardService")
    @CircuitBreaker(name = "cardService")
    @GetMapping("/payment/{paymentId}")
    Card findByPayment(@PathVariable("paymentId") Long paymentId);
}
