package vidaSalud.msbff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("Admin") 
                .requestMatchers(HttpMethod.GET,"/api/appointments/**").hasAnyRole("Admin","Recepcionista","Paciente")
                .requestMatchers(HttpMethod.POST,"/api/appointments/**").hasAnyRole("Recepcionista","Paciente", "Admin")
                .requestMatchers(HttpMethod.PUT,"/api/appointments/**").hasAnyRole("Admin","Recepcionista")
                .requestMatchers(HttpMethod.GET,"/api/catalog/**").hasAnyRole("Admin","Recepcionista")
                .requestMatchers("/api/catalog/**").hasAnyRole("Admin")
                .requestMatchers("/api/notify/**").hasAnyRole( "Recepcionista","Paciente")
                .requestMatchers("/api/report/**").hasAnyRole( "Admin")
                .requestMatchers(HttpMethod.GET,"/api/audit/**").hasAnyRole("Admin", "Auditor")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
