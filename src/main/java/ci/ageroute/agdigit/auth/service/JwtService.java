package ci.ageroute.agdigit.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Fabrication et verification des jetons JWT.
 *
 * <p>Le jeton porte l'identifiant de l'utilisateur et son role, et il est signe
 * avec une cle secrete. Le serveur ne stocke aucune session : il lui suffit de
 * verifier la signature pour savoir que le jeton vient bien de lui et qu'il n'a
 * pas ete altere.
 */
@Service
public class JwtService {

    private final SecretKey cle;
    private final Duration duree;

    public JwtService(
            @Value("${agdigit.jwt.secret}") String secret,
            @Value("${agdigit.jwt.duree-heures:12}") long dureeHeures) {
        // HMAC-SHA256 exige une cle d'au moins 256 bits, soit 32 caracteres.
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "agdigit.jwt.secret doit faire au moins 32 caracteres");
        }
        this.cle = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.duree = Duration.ofHours(dureeHeures);
    }

    public String creer(String identifiant, String role) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(identifiant)
                .claim("role", role)
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(duree)))
                .signWith(cle)
                .compact();
    }

    /** Retourne l'identifiant porte par le jeton, ou null s'il est invalide ou expire. */
    public String identifiantSiValide(String jeton) {
        try {
            Claims contenu = Jwts.parser().verifyWith(cle).build()
                    .parseSignedClaims(jeton).getPayload();
            return contenu.getSubject();
        } catch (JwtException | IllegalArgumentException erreur) {
            return null;
        }
    }

    public long dureeSecondes() {
        return duree.toSeconds();
    }
}
