package ci.ageroute.agdigit.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Compte de connexion a la demonstration.
 *
 * <p>Cette table nous appartient : elle ne fait pas partie du schema AGDIGIT.
 * Le backend de demonstration n'a pas acces au referentiel d'utilisateurs de la
 * plateforme, on gere donc nos propres comptes.
 *
 * <p>{@code motDePasse} contient une EMPREINTE BCrypt, jamais le mot de passe.
 */
@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifiant", nullable = false)
    private String identifiant;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(name = "nom_complet")
    private String nomComplet;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "cree_le")
    private Instant creeLe;

    @Column(name = "derniere_connexion")
    private Instant derniereConnexion;
}
