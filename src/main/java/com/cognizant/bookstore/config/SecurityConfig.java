package com.cognizant.bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration

@EnableMethodSecurity

public class SecurityConfig {
 
    @Autowired
    @Lazy
    private JwtRequestFilter jwtAuthFilter;
 
    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)//cross-site-requeust-forgery

            .sessionManagement(session -> session.sessionCreationPolicy
            		(SessionCreationPolicy.STATELESS))

//            .authorizeHttpRequests(auth -> auth
//
//                .requestMatchers("/api/auth/**").permitAll()
//
//                .requestMatchers("/api/admin/dashboard").hasRole("ADMIN")
//
//                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
//
//                .anyRequest().authenticated()
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") 
            	    .requestMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") 
            	    .anyRequest().authenticated()
            	)


            

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
 
        return http.build();

    }
 
    @Bean

	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();

	}
    @Bean

	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

		return config.getAuthenticationManager();

	}


}

 
