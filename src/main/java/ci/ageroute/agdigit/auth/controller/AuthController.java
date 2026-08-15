package ci.ageroute.agdigit.auth.controller;

import ci.ageroute.agdigit.auth.dto.DemandeConnexionDto;
import ci.ageroute.agdigit.auth.dto.ReponseConnexionDto;
import ci.ageroute.agdigit.auth.dto.UtilisateurDto;
import ci.ageroute.agdigit.auth.service.AuthService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentification.
 *
 * <pre>
 *   POST /api/auth/connexion   { "identifiant": "...", "motDePasse": "..." }
 *   GET  /api/auth/moi         (avec l'en-tete Authorization: Bearer &lt;jeton&gt;)
 * </pre>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/connexion")
    public ReponseConnexionDto connexion(@Valid @RequestBody DemandeConnexionDto demande) {
        return service.connecter(demande.identifiant(), demande.motDePasse());
    }

    /** Permet au frontend de verifier au demarrage que son jeton est toujours valide. */
    @GetMapping("/moi")
    public UtilisateurDto moi(Principal principal) {
        return service.parIdentifiant(principal.getName());
    }
}
