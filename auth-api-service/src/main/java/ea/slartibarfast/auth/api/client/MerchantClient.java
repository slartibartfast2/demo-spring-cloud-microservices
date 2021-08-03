package ea.slartibarfast.auth.api.client;

import ea.slartibarfast.auth.api.model.vo.MerchantVo;
import ea.slartibarfast.auth.api.model.AuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "merchant-api-service")
public interface MerchantClient {

    @GetMapping("/{merchantId}")
    MerchantVo retrieveById(@PathVariable("merchantId") Long merchantId);

    @PostMapping("/auth")
    MerchantVo createMerchantAuth(@RequestBody AuthRequest authRequest);
}
