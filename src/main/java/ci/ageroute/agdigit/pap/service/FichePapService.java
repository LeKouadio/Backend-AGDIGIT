package ci.ageroute.agdigit.pap.service;

import ci.ageroute.agdigit.common.RessourceIntrouvableException;
import ci.ageroute.agdigit.pap.dto.FichePapResumeDto;
import ci.ageroute.agdigit.pap.entity.FichePap;
import ci.ageroute.agdigit.pap.repository.FichePapRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private final FichePapRepository depot;

    public FichePapService(FichePapRepository depot) {
        this.depot = depot;
    }

    /** Liste paginee, filtrable par commune ou par fragment de nom. */
    public Page<FichePapResumeDto> lister(String commune, String nom, Pageable pagination) {
        Page<FichePap> fiches;
        if (commune != null && !commune.isBlank()) {
            fiches = depot.findByCommuneIgnoreCase(commune.trim(), pagination);
        } else if (nom != null && !nom.isBlank()) {
            fiches = depot.findByNomPapContainingIgnoreCase(nom.trim(), pagination);
        } else {
            fiches = depot.findAll(pagination);
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
