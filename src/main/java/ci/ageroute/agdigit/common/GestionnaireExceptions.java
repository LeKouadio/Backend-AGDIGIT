package ci.ageroute.agdigit.common;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduit les exceptions en reponses JSON coherentes.
 *
 * <p>Sans cela, une ressource absente renvoie une page d'erreur HTML que le
 * frontend ne sait pas exploiter. Ici toute erreur ressort au meme format.
 */
@RestControllerAdvice
public class GestionnaireExceptions {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<Map<String, Object>> introuvable(RessourceIntrouvableException erreur) {
        return reponse(HttpStatus.NOT_FOUND, erreur.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> requeteInvalide(IllegalArgumentException erreur) {
        return reponse(HttpStatus.BAD_REQUEST, erreur.getMessage());
    }

    /** Identifiants refuses : 401, avec le message tel quel pour l'afficher au formulaire. */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> identifiantsRefuses(BadCredentialsException erreur) {
        return reponse(HttpStatus.UNAUTHORIZED, erreur.getMessage());
    }

    /** Echecs de validation @Valid : on concatene les messages des champs fautifs. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException erreur) {
        String message = erreur.getBindingResult().getFieldErrors().stream()
                .map(champ -> champ.getDefaultMessage())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
        return reponse(HttpStatus.BAD_REQUEST,
                message.isBlank() ? "Requête invalide" : message);
    }

    private ResponseEntity<Map<String, Object>> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of(
                "horodatage", Instant.now().toString(),
                "statut", statut.value(),
                "erreur", statut.getReasonPhrase(),
                "message", message == null ? "" : message));
    }
}
