package com.example.demo.artifactsmanager.configuration.security;

import com.example.demo.artifactsmanager.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${allowedUsers}")
    private String allowedUsers;

    @Value("${adminUser}")
    private String adminUser;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        // Create in-memory authentication for normal users
        Arrays.stream(allowedUsers.split(","))
                .map(String::trim)
                .forEach(user -> {
                    try {
                        auth.inMemoryAuthentication()
                                .withUser(user)
                                .password(passwordEncoder.encode("password"))
                                .roles(Role.VIEW.getRole());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        // Create admin user
        auth.inMemoryAuthentication()
                .withUser(adminUser)
                .password(passwordEncoder.encode("passwordadmin"))
                .roles(Role.EDIT.getRole());
    }
}
