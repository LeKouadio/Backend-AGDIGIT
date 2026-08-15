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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Points d'entree HTTP des fiches PAP.
 *
 * <p>A tester dans Postman :
 * <pre>
 *   GET http://localhost:8080/api/paps
 *   GET http://localhost:8080/api/paps?page=0&amp;size=10&amp;champ=nomPap&amp;valeur=KOUASSI
 *   GET http://localhost:8080/api/paps?champ=identifiantPap&amp;valeur=DJ1
 *   GET http://localhost:8080/api/paps/detail?identifiant=PVS/S1/CO/DJ1/MEN/000000367
 *   GET http://localhost:8080/api/paps/criteres
 *   GET http://localhost:8080/api/paps/communes
 *   GET http://localhost:8080/api/paps/statistiques
 * </pre>
 *
 * <p>L'identifiant PAP contient des {@code /} : il passe donc en PARAMETRE et
 * jamais dans le chemin. Spring Security refuse les {@code %2F} dans un segment
 * d'URL et repondrait 400.
 */
@RestController
@RequestMapping("/api/paps")
public class FichePapController {

    private final FichePapService service;

    public FichePapController(FichePapService service) {
        this.service = service;
    }

    /**
     * Liste paginee. {@code champ} designe le critere de recherche et
     * {@code valeur} le texte cherche, comme le selecteur de la plateforme.
     * Les parametres page, size et sort sont geres par Spring.
     */
    @GetMapping
    public Page<FichePapResumeDto> lister(
            @RequestParam(required = false, defaultValue = "identifiantPap") String champ,
            @RequestParam(required = false) String valeur,
            @PageableDefault(size = 10, sort = "identifiantPap", direction = Sort.Direction.ASC)
            Pageable pagination) {
        return service.lister(champ, valeur, pagination);
    }

    /** Criteres proposes dans le selecteur de recherche. */
    @GetMapping("/criteres")
    public List<FichePapService.CritereRecherche> criteres() {
        return service.criteres();
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

    /**
     * Fiche complete, avec ses 71 colonnes metier.
     *
     * <p>L'identifiant passe en parametre : il contient des {@code /} et Spring
     * Security refuse les {@code %2F} dans un segment de chemin.
     */
    @GetMapping("/detail")
    public FichePap detail(@RequestParam("identifiant") String identifiantPap) {
        return service.parIdentifiant(identifiantPap);
    }
}
