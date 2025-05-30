package br.com.kitchen.backend.service;

import br.com.kitchen.backend.model.User;
import br.com.kitchen.backend.repository.UserRepository;
import br.com.kitchen.backend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String strUser) throws UsernameNotFoundException {
        User user = userRepository.findByUser(strUser)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + strUser));

        return new CustomUserDetails(user,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
