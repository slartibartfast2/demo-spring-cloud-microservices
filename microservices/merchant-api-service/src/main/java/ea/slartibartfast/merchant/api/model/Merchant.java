package ea.slartibartfast.merchant.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Merchant {

    private Long id;
    private String key;
    private String email;
    private String password;
    private String role;

    public Merchant(String key, String email, String password, String role) {
        this.key = key;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
