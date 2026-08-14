package ci.ageroute.agdigit.common;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private ResponseEntity<Map<String, Object>> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of(
                "horodatage", Instant.now().toString(),
                "statut", statut.value(),
                "erreur", statut.getReasonPhrase(),
                "message", message == null ? "" : message));
    }
}
