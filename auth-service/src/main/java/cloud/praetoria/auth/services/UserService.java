package cloud.praetoria.auth.services;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cloud.praetoria.auth.entities.User;
import cloud.praetoria.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String ypareoId) throws UsernameNotFoundException {
        User user = userRepository.findByYpareoId(ypareoId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        System.out.println("------------------ " + authority.getAuthority());
        return new org.springframework.security.core.userdetails.User(
        	    user.getYpareoId(),
        	    user.getPassword(),
        	    List.of(authority)
        	);
    }
}
