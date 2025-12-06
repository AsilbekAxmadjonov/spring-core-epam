package org.example.security.service;

import lombok.RequiredArgsConstructor;
import org.example.security.GymUserDetails;
import org.example.services.UserEntityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GymUserDetailsService implements UserDetailsService {

    private final UserEntityService userEntityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new GymUserDetails(
                userEntityService.getByUsername(username)
        );
    }
}
