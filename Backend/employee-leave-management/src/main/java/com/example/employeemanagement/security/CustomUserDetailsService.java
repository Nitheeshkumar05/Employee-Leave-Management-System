package com.example.employeemanagement.security;

import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository repository;
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var e=repository.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return User.withUsername(e.getEmail()).password(e.getPassword()).roles(e.getRole().name()).build();
    }
}
