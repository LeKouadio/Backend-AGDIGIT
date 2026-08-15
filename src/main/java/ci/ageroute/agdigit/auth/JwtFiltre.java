package ci.ageroute.agdigit.auth;

import ci.ageroute.agdigit.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lit l'en-tete {@code Authorization: Bearer <jeton>} et, si le jeton est
 * valide, declare l'utilisateur comme authentifie pour la duree de la requete.
 *
 * <p>Un jeton absent ou invalide n'est pas une erreur ici : le filtre laisse
 * simplement passer sans authentifier, et c'est la configuration de securite
 * qui decidera si la route exigeait une authentification.
 */
@Component
public class JwtFiltre extends OncePerRequestFilter {

    private static final String PREFIXE = "Bearer ";

    private final JwtService jwt;

    public JwtFiltre(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest requete,
                                    @NonNull HttpServletResponse reponse,
                                    @NonNull FilterChain chaine)
            throws ServletException, IOException {

        String entete = requete.getHeader("Authorization");
        if (entete != null && entete.startsWith(PREFIXE)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String identifiant = jwt.identifiantSiValide(entete.substring(PREFIXE.length()));
            if (identifiant != null) {
                var authentification = new UsernamePasswordAuthenticationToken(
                        identifiant, null, List.of());
                authentification.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(requete));
                SecurityContextHolder.getContext().setAuthentication(authentification);
            }
        }
        chaine.doFilter(requete, reponse);
    }
}
