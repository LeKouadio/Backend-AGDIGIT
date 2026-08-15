package ci.ageroute.agdigit.fiche;

import ci.ageroute.agdigit.common.RessourceIntrouvableException;
import ci.ageroute.agdigit.fiche.RegistreEntites.Colonne;
import ci.ageroute.agdigit.fiche.RegistreEntites.Onglet;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fiche d'enquete : les 12 onglets d'une PAP.
 *
 * <pre>
 *   GET /api/fiches/onglets
 *       la liste des onglets et, pour chacun, ses colonnes et leurs libelles
 *
 *   GET /api/fiches/{codeOnglet}?identifiant=PVS/S1/CO/DJ1/MEN/000000367
 *       les colonnes et les donnees d'un onglet pour une PAP
 * </pre>
 *
 * <p>L'identifiant passe en PARAMETRE et non dans le chemin : il contient des
 * {@code /}, et Spring Security refuse les {@code %2F} dans un segment d'URL
 * (reponse 400). Un identifiant metier de cette forme n'a pas sa place dans un
 * chemin.
 */
@RestController
@RequestMapping("/api/fiches")
public class FicheController {

    /** Onglet allege pour la liste : sans les colonnes. */
    public record OngletResumeDto(String code, String libelle, boolean multiple) {}

    /** Contenu d'un onglet pour une PAP. */
    public record ContenuOngletDto(
            String code,
            String libelle,
            boolean multiple,
            List<Colonne> colonnes,
            List<Map<String, Object>> lignes,
            int total) {}

    private final RegistreEntites registre;
    private final FicheRepository depot;

    public FicheController(RegistreEntites registre, FicheRepository depot) {
        this.registre = registre;
        this.depot = depot;
    }

    /** Structure des onglets, appelee une seule fois au chargement de l'ecran. */
    @GetMapping("/onglets")
    public List<OngletResumeDto> onglets() {
        return registre.onglets().stream()
                .map(o -> new OngletResumeDto(o.code(), o.libelle(), o.multiple()))
                .toList();
    }

    /** Colonnes et donnees d'un onglet pour une PAP. */
    @GetMapping("/{codeOnglet}")
    public ContenuOngletDto contenu(@PathVariable String codeOnglet,
                                    @RequestParam("identifiant") String identifiantPap) {
        Onglet onglet = registre.onglet(codeOnglet);
        if (onglet == null) {
            throw new RessourceIntrouvableException("Onglet inconnu : " + codeOnglet);
        }
        List<Map<String, Object>> lignes = depot.lignes(onglet, identifiantPap);
        return new ContenuOngletDto(
                onglet.code(), onglet.libelle(), onglet.multiple(),
                onglet.colonnes(), lignes, lignes.size());
    }
}
