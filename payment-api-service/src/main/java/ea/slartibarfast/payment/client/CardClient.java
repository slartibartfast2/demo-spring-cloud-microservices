package ea.slartibarfast.payment.client;

import ea.slartibarfast.payment.model.Card;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "card-api-service")
public interface CardClient {

    @GetMapping("/payment/{paymentId}")
    Card findByPayment(@PathVariable("paymentId") Long paymentId);
}
