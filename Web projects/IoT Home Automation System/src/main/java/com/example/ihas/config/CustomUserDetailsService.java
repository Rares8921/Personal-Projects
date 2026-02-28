package com.example.ihas.config;

import com.example.ihas.dao.UserDAO;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.example.ihas.models.User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(String.valueOf(user.getId()))
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(Collections.emptyList())
                .build();

    }
}
