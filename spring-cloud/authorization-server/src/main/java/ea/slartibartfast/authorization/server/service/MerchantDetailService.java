package ea.slartibartfast.authorization.server.service;

import ea.slartibartfast.authorization.server.client.MerchantClient;
import ea.slartibartfast.authorization.server.model.MerchantVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantDetailService implements UserDetailsService {

    private final MerchantClient merchantClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long merchantId = Long.valueOf(username);
        MerchantVo merchantVo = merchantClient.findByMerchantId(merchantId).orElseThrow(() -> new UsernameNotFoundException("No Merchant Found"));
        log.info("Merchant found: {}", merchantVo.toString());
        return User.builder()
                   .username(merchantVo.getKey())
                   .password(merchantVo.getPassword())
                   .accountExpired(false)
                   .accountLocked(false)
                   .credentialsExpired(false)
                   .disabled(false)
                   .authorities(getAuthorities())
                   .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of("read", "write")
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toList());
    }
}
