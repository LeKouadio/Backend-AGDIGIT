package ci.ageroute.agdigit.pap.service;

import ci.ageroute.agdigit.common.RessourceIntrouvableException;
import ci.ageroute.agdigit.pap.dto.FichePapResumeDto;
import ci.ageroute.agdigit.pap.entity.FichePap;
import ci.ageroute.agdigit.pap.repository.FichePapRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regles metier autour des fiches PAP.
 *
 * <p>Cette couche existe pour que le controleur ne fasse que du HTTP et que le
 * repository ne fasse que du SQL. C'est ici qu'on filtre, qu'on convertit en DTO
 * et qu'on decidera plus tard des regles d'acces.
 */
@Service
@Transactional(readOnly = true)
public class FichePapService {

    /** Un critere de recherche propose dans le selecteur de la liste. */
    public record CritereRecherche(String code, String libelle) {}

    private final FichePapRepository depot;

    /**
     * Champs sur lesquels la recherche est autorisee.
     *
     * <p>Une liste blanche plutot qu'un nom de champ libre : le code recu ne
     * touche jamais la requete, il selectionne une methode deja ecrite.
     */
    private final Map<String, BiFunction<String, Pageable, Page<FichePap>>> recherches =
            new LinkedHashMap<>();

    private final List<CritereRecherche> criteres = List.of(
            new CritereRecherche("identifiantPap", "Identifiant du pap"),
            new CritereRecherche("nomPap", "Nom du pap"),
            new CritereRecherche("commune", "Commune de l'enquêté"),
            new CritereRecherche("quartier", "Quartier de l'enquêté"),
            new CritereRecherche("enqueteur", "Nom de l'enquêteur"),
            new CritereRecherche("numPiece", "Numéro de la pièce"),
            new CritereRecherche("telephone1", "Numéro de téléphone principal"));

    public FichePapService(FichePapRepository depot) {
        this.depot = depot;
        recherches.put("identifiantPap", depot::findByIdentifiantPapContainingIgnoreCase);
        recherches.put("nomPap", depot::findByNomPapContainingIgnoreCase);
        recherches.put("commune", depot::findByCommuneContainingIgnoreCase);
        recherches.put("quartier", depot::findByQuartierContainingIgnoreCase);
        recherches.put("enqueteur", depot::findByEnqueteurContainingIgnoreCase);
        recherches.put("numPiece", depot::findByNumPieceContainingIgnoreCase);
        recherches.put("telephone1", depot::findByTelephone1ContainingIgnoreCase);
    }

    /** Criteres proposes dans le selecteur, dans l'ordre d'affichage. */
    public List<CritereRecherche> criteres() {
        return criteres;
    }

    /**
     * Liste paginee. {@code champ} designe le critere, {@code valeur} le texte
     * cherche ; sans valeur, la liste complete est renvoyee.
     */
    public Page<FichePapResumeDto> lister(String champ, String valeur, Pageable pagination) {
        Page<FichePap> fiches;
        if (valeur == null || valeur.isBlank()) {
            fiches = depot.findAll(pagination);
        } else {
            var recherche = recherches.get(champ == null ? "identifiantPap" : champ);
            if (recherche == null) {
                throw new IllegalArgumentException("Critère de recherche inconnu : " + champ);
            }
            fiches = recherche.apply(valeur.trim(), pagination);
        }
        return fiches.map(FichePapResumeDto::depuis);
    }

    /** Fiche complete. Leve une exception si l'identifiant est inconnu. */
    public FichePap parIdentifiant(String identifiantPap) {
        return depot.findById(identifiantPap)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Aucune PAP avec l'identifiant " + identifiantPap));
    }

    public List<String> communes() {
        return depot.listerCommunes();
    }

    /** Indicateurs simples pour un tableau de bord. */
    public Map<String, Object> statistiques() {
        Map<String, Long> parCommune = new LinkedHashMap<>();
        for (Object[] ligne : depot.compterParCommune()) {
            parCommune.put((String) ligne[0], ((Number) ligne[1]).longValue());
        }
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("totalPap", depot.count());
        resultat.put("parCommune", parCommune);
        return resultat;
    }
}
