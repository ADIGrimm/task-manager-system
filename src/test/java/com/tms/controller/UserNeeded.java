package com.tms.controller;

import com.tms.model.Role;
import com.tms.model.User;
import java.util.List;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface UserNeeded {
    default UsernamePasswordAuthenticationToken createAuthToken() {
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        role.setId(1L);

        User customUser = new User();
        customUser.setId(1L);
        customUser.setEmail("test@example.com");
        customUser.setPassword("password");
        customUser.setFirstName("Test");
        customUser.setLastName("User");
        customUser.setRoles(Set.of(role));
        List<SimpleGrantedAuthority> authorities = customUser.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                .toList();

        return new UsernamePasswordAuthenticationToken(customUser, null, authorities);
    }
}
