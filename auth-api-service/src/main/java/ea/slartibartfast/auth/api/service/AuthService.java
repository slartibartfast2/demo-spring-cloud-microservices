package ea.slartibartfast.auth.api.service;

import ea.slartibartfast.auth.api.client.MerchantClient;
import ea.slartibartfast.auth.api.model.AuthRequest;
import ea.slartibartfast.auth.api.model.AuthResponse;
import ea.slartibartfast.auth.api.model.vo.MerchantVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MerchantClient merchantClient;
    private final JwtUtil jwt;

    public AuthResponse register(AuthRequest authRequest) {
        //do validation if user already exists
        authRequest.setPassword(BCrypt.hashpw(authRequest.getPassword(), BCrypt.gensalt()));

        MerchantVo merchantVo = merchantClient.createMerchantAuth(authRequest);

        String accessToken = jwt.generate(merchantVo, "ACCESS");
        String refreshToken = jwt.generate(merchantVo, "REFRESH");

        return new AuthResponse(accessToken, refreshToken);

    }
}
