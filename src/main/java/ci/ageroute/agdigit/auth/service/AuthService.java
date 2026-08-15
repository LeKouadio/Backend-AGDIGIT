package ci.ageroute.agdigit.auth.service;

import ci.ageroute.agdigit.auth.dto.ReponseConnexionDto;
import ci.ageroute.agdigit.auth.dto.UtilisateurDto;
import ci.ageroute.agdigit.auth.entity.Utilisateur;
import ci.ageroute.agdigit.auth.repository.UtilisateurRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurRepository depot;
    private final PasswordEncoder encodeur;
    private final JwtService jwt;

    public AuthService(UtilisateurRepository depot, PasswordEncoder encodeur, JwtService jwt) {
        this.depot = depot;
        this.encodeur = encodeur;
        this.jwt = jwt;
    }

    /**
     * Verifie les identifiants et delivre un jeton.
     *
     * <p>Le message d'erreur est volontairement le meme que l'identifiant soit
     * inconnu ou le mot de passe faux : distinguer les deux permettrait de
     * decouvrir quels comptes existent.
     */
    @Transactional
    public ReponseConnexionDto connecter(String identifiant, String motDePasse) {
        Optional<Utilisateur> trouve = depot.parIdentifiant(identifiant);

        if (trouve.isEmpty() || !encodeur.matches(motDePasse, trouve.get().getMotDePasse())) {
            throw new BadCredentialsException("Identifiant ou mot de passe incorrect");
        }
        Utilisateur utilisateur = trouve.get();
        if (!utilisateur.isActif()) {
            throw new BadCredentialsException("Ce compte est désactivé");
        }

        utilisateur.setDerniereConnexion(Instant.now());

        return new ReponseConnexionDto(
                jwt.creer(utilisateur.getIdentifiant(), utilisateur.getRole()),
                jwt.dureeSecondes(),
                versDto(utilisateur));
    }

    @Transactional(readOnly = true)
    public UtilisateurDto parIdentifiant(String identifiant) {
        return depot.parIdentifiant(identifiant)
                .map(AuthService::versDto)
                .orElseThrow(() -> new BadCredentialsException("Utilisateur inconnu"));
    }

    private static UtilisateurDto versDto(Utilisateur utilisateur) {
        return new UtilisateurDto(
                utilisateur.getIdentifiant(),
                utilisateur.getNomComplet(),
                utilisateur.getRole());
    }
}
