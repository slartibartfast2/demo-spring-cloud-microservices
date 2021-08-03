package ea.slartibarfast.merchant.api.controller;

import ea.slartibarfast.merchant.api.manager.MerchantManager;
import ea.slartibarfast.merchant.api.model.AuthRequest;
import ea.slartibarfast.merchant.api.model.CreateMerchantResponse;
import ea.slartibarfast.merchant.api.model.vo.MerchantVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MerchantController {

    private final MerchantManager merchantManager;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/auth", produces = "application/json")
    public CreateMerchantResponse createMerchant(@RequestBody AuthRequest authRequest) {
        return merchantManager.createMerchant(authRequest);
    }

    @GetMapping
    public List<MerchantVo> retrieveAllMerchants() {
        return merchantManager.retrieveAllMerchants();
    }

    @GetMapping("/{id}")
    public MerchantVo retrieveMerchantById(@PathVariable("id") Long id) {
        return merchantManager.retrieveMerchant(id);
    }
}
