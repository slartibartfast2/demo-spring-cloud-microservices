package ea.slartibarfast.merchant.api.repository;

import ea.slartibarfast.merchant.api.model.Merchant;

import java.util.ArrayList;
import java.util.List;

public class MerchantRepository {

    private static List<Merchant> merchants = new ArrayList<>();

    public Merchant add(Merchant merchant) {
        merchant.setId((long) (merchants.size() + 1));
        merchants.add(merchant);
        return merchant;
    }

    public Merchant findById(Long id) {
        return merchants.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Merchant> findAll() {
        return merchants;
    }
}
