package ci.ageroute.agdigit.pap.controller;

import ci.ageroute.agdigit.pap.dto.FichePapResumeDto;
import ci.ageroute.agdigit.pap.entity.FichePap;
import ci.ageroute.agdigit.pap.service.FichePapService;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Points d'entree HTTP des fiches PAP.
 *
 * <p>A tester dans Postman :
 * <pre>
 *   GET http://localhost:8080/api/paps
 *   GET http://localhost:8080/api/paps?page=0&amp;size=20&amp;commune=COCODY
 *   GET http://localhost:8080/api/paps?nom=KOUASSI
 *   GET http://localhost:8080/api/paps/PVS%2FS1%2FCO%2FDJ1%2FMEN%2F000000367
 *   GET http://localhost:8080/api/paps/communes
 *   GET http://localhost:8080/api/paps/statistiques
 * </pre>
 *
 * <p>Attention : l'identifiant PAP contient des {@code /}. Dans une URL il doit
 * etre encode en {@code %2F}, sinon le serveur le lit comme plusieurs segments
 * de chemin. Postman propose de le faire automatiquement.
 */
@RestController
@RequestMapping("/api/paps")
public class FichePapController {

    private final FichePapService service;

    public FichePapController(FichePapService service) {
        this.service = service;
    }

    /** Liste paginee. Les parametres page, size et sort sont geres par Spring. */
    @GetMapping
    public Page<FichePapResumeDto> lister(
            @RequestParam(required = false) String commune,
            @RequestParam(required = false) String nom,
            @PageableDefault(size = 20, sort = "identifiantPap", direction = Sort.Direction.ASC)
            Pageable pagination) {
        return service.lister(commune, nom, pagination);
    }

    /** Valeurs distinctes de commune, pour alimenter un menu deroulant. */
    @GetMapping("/communes")
    public List<String> communes() {
        return service.communes();
    }

    /** Indicateurs agreges pour le tableau de bord. */
    @GetMapping("/statistiques")
    public Map<String, Object> statistiques() {
        return service.statistiques();
    }

    /** Fiche complete, avec ses 71 colonnes metier. */
    @GetMapping("/{identifiantPap}")
    public FichePap detail(@PathVariable String identifiantPap) {
        return service.parIdentifiant(identifiantPap);
    }
}
