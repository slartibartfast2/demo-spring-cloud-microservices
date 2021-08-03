package ea.slartibarfast.merchant.api.model;

import lombok.*;

@Getter
@Setter
public class AuthRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
