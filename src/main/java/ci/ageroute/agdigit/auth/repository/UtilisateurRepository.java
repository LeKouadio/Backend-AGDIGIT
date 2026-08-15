package ci.ageroute.agdigit.auth.repository;

import ci.ageroute.agdigit.auth.entity.Utilisateur;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /** Recherche insensible a la casse : l'utilisateur ne doit pas avoir a s'en soucier. */
    @Query("select u from Utilisateur u where lower(u.identifiant) = lower(:identifiant)")
    Optional<Utilisateur> parIdentifiant(String identifiant);
}
