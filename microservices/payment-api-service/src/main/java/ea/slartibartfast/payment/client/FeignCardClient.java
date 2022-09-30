package ea.slartibartfast.payment.client;

import ea.slartibartfast.payment.model.Card;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "card-api-service")
@ConditionalOnProperty(value = "mock.card.client.enabled", havingValue = "false", matchIfMissing = true)
public interface FeignCardClient extends CardClient {

    @GetMapping("/payment/{paymentId}")
    Card findByPayment(@PathVariable("paymentId") long paymentId,
                       @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                       @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);
}
