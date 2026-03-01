package com.example.Medicine.Drug_OS.Configurations;

import com.example.Medicine.Drug_OS.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Publicly accessible pages
                        .requestMatchers("/", "/login", "/register", "/seller/registration", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/products.html", "/api/v1/product/customer/**").permitAll()

                        // 2. Rules for any authenticated user (REGULAR, SELLER, ADMIN)
                        .requestMatchers("/api/cart/**", "/checkout.html", "/payments/process").hasAnyRole("REGULAR", "SELLER", "ADMIN")

                        // 3. Rules for both Admins and Sellers
                        .requestMatchers("/product-admin.html", "/api/v1/product/admin/**").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/seller-dashboard.html", "/api/seller/profile").hasRole("SELLER")

                        // 4. Rules for ADMINS ONLY
                        .requestMatchers("/admin-dashboard.html", "/admin-usermanagement.html", "/admin-expiryalerts.html", "/api/admin/**", "/api/alerts/**").hasAnyRole("ADMIN", "SELLER")

                        // 5. All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin-dashboard.html");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"))) {
                                // Corrected redirect for sellers
                                response.sendRedirect("/product-admin.html");
                            } else {
                                response.sendRedirect("/products.html");
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:63342", "http://127.0.0.1:63342"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}