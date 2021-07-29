package ea.slartibarfast.payment.model;

import lombok.Data;

@Data
public class Merchant {
    private Long id;
    private String name;
    private String key;
}
