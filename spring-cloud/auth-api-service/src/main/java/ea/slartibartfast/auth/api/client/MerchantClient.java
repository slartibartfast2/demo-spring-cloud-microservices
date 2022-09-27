package ea.slartibartfast.auth.api.client;

import ea.slartibartfast.auth.api.model.vo.MerchantVo;
import ea.slartibartfast.auth.api.model.AuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MerchantClient {

    MerchantVo retrieveById(Long merchantId);

    MerchantVo createMerchantAuth(AuthRequest authRequest);
}
