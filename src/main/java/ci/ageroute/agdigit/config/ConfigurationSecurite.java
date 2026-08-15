package ci.ageroute.agdigit.config;

import ci.ageroute.agdigit.auth.JwtFiltre;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Securite de l'API.
 *
 * <ul>
 *   <li>sans session : chaque requete porte son jeton, rien n'est garde en memoire ;</li>
 *   <li>CSRF desactive : sans cookie de session, il n'y a rien a voler ;</li>
 *   <li>tout /api/** exige un jeton, sauf la connexion.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class ConfigurationSecurite {

    private final JwtFiltre jwtFiltre;

    @Value("${agdigit.cors.origines:http://localhost:4200}")
    private String[] origines;

    public ConfigurationSecurite(JwtFiltre jwtFiltre) {
        this.jwtFiltre = jwtFiltre;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain chaine(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(sourceCors()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(routes -> routes
                    .requestMatchers("/api/auth/connexion").permitAll()
                    // les requetes preliminaires CORS ne portent jamais de jeton
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll())
            // renvoie 401 au lieu de rediriger vers une page de connexion HTML,
            // que le frontend ne saurait pas interpreter
            .exceptionHandling(e -> e.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtFiltre, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource sourceCors() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(origines));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
