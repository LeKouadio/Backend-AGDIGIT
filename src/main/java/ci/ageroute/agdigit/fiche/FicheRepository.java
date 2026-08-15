package ci.ageroute.agdigit.fiche;

import ci.ageroute.agdigit.fiche.RegistreEntites.Colonne;
import ci.ageroute.agdigit.fiche.RegistreEntites.Onglet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Lecture generique des tables AGDIGIT.
 *
 * <p>Une classe JPA par table serait 12 fois le meme code : le formulaire etant
 * pilote par les metadonnees, l'acces l'est aussi. On compose donc le SQL a
 * partir du {@link RegistreEntites}.
 *
 * <p>Securite : les noms de table et de colonne viennent EXCLUSIVEMENT du
 * registre, jamais d'un parametre de requete, et le registre a verifie leur
 * forme au demarrage. La seule valeur venant de l'exterieur, l'identifiant de
 * la PAP, passe par un parametre nomme.
 */
@Repository
public class FicheRepository {

    private final JdbcClient jdbc;

    public FicheRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Lignes d'un onglet pour une PAP donnee, colonnes dans l'ordre du registre. */
    public List<Map<String, Object>> lignes(Onglet onglet, String identifiantPap) {
        String colonnes = onglet.colonnes().stream()
                .map(Colonne::nom)
                .collect(Collectors.joining(", "));

        String sql = "select " + colonnes
                + " from public." + onglet.table()
                + " where identifiant_pap = :identifiant";
        if (onglet.multiple()) {
            sql += " order by nom_membre_men";
        }

        return jdbc.sql(sql)
                .param("identifiant", identifiantPap)
                .query()
                .listOfRows()
                .stream()
                .map(this::ordonner)
                .toList();
    }

    /** Nombre de lignes d'un onglet pour une PAP. */
    public int compter(Onglet onglet, String identifiantPap) {
        return jdbc.sql("select count(*) from public." + onglet.table()
                        + " where identifiant_pap = :identifiant")
                .param("identifiant", identifiantPap)
                .query(Integer.class)
                .single();
    }

    /**
     * PostgreSQL renvoie les cles en minuscules et sans ordre garanti ;
     * on reconstruit une LinkedHashMap pour que le frontend recoive les
     * colonnes dans l'ordre d'affichage.
     */
    private Map<String, Object> ordonner(Map<String, Object> ligne) {
        Map<String, Object> ordonnee = new LinkedHashMap<>();
        ligne.forEach((cle, valeur) -> ordonnee.put(cle.toLowerCase(), valeur));
        return ordonnee;
    }
}
