package ci.ageroute.agdigit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Autorise le frontend Angular a appeler cette API.
 *
 * <p>Sans cela le navigateur bloque les requetes : le front tourne sur
 * localhost:4200 et l'API sur localhost:8080, ce sont deux origines
 * differentes. C'est une regle du navigateur, pas de Spring — d'ou l'erreur
 * "CORS policy" que l'on voit dans la console du navigateur et jamais dans
 * Postman, qui n'applique pas cette regle.
 */
@Configuration
public class ConfigurationCors implements WebMvcConfigurer {

    @Value("${agdigit.cors.origines:http://localhost:4200}")
    private String[] origines;

    @Override
    public void addCorsMappings(CorsRegistry registre) {
        registre.addMapping("/api/**")
                .allowedOrigins(origines)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
