package ea.slartibartfast.auth.api.client;

import ea.slartibartfast.auth.api.model.AuthRequest;
import ea.slartibartfast.auth.api.model.vo.MerchantVo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "merchant-api-service")
@ConditionalOnProperty(value = "mock.merchant.client.enabled", havingValue = "false", matchIfMissing = true)
public interface FeignMerchantClient extends MerchantClient {

    @GetMapping("/{merchantId}")
    MerchantVo retrieveById(@PathVariable("merchantId") Long merchantId);

    @PostMapping("/auth")
    MerchantVo createMerchantAuth(@RequestBody AuthRequest authRequest);
}
