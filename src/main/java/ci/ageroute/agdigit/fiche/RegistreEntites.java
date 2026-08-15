package ci.ageroute.agdigit.fiche;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
// Spring Boot 4 embarque Jackson 3 : le paquet est `tools.jackson`, plus `com.fasterxml.jackson`.
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Catalogue des entites AGDIGIT : pour chaque onglet, la table correspondante
 * et la liste de ses colonnes metier avec leur libelle.
 *
 * <p>Les libelles proviennent des DESCRIPTIONS declarees dans AGDIGIT, extraites
 * dans {@code src/main/resources/entites/}. C'est la meme source que celle qui
 * alimente les ecrans de la plateforme : le formulaire n'est pas dessine a la
 * main, il se deduit des metadonnees. L'ordre des colonnes est celui du
 * fichier, qui est aussi l'ordre d'affichage de la plateforme.
 *
 * <p>Ce registre sert aussi de LISTE BLANCHE : les requetes de {@link FicheRepository}
 * composent du SQL avec des noms de table et de colonne, jamais avec des valeurs
 * saisies. Tout identifiant qui n'est pas declare ici est refuse.
 */
@Component
public class RegistreEntites {

    /** Un onglet de la fiche d'enquete. */
    public record Onglet(
            String code,
            String libelle,
            String table,
            boolean multiple,
            List<Colonne> colonnes) {}

    /** Une case du formulaire. */
    public record Colonne(String nom, String libelle, String type) {}

    /**
     * Onglets dans l'ordre de la plateforme. Les libelles sont ceux des
     * descriptions d'entites AGDIGIT, releves sur l'ecran de production.
     *
     * <p>{@code multiple} distingue les tables a plusieurs lignes par PAP :
     * seule {@code is_chef} est dans ce cas, sa cle etant le couple
     * (identifiant_pap, nom_membre_men).
     */
    private static final List<Onglet> DEFINITIONS = List.of(
            new Onglet("informations", "Informations", "ag_survey_sheet_test", false, List.of()),
            new Onglet("membres", "membre du ménage", "is_chef", true, List.of()),
            new Onglet("equipement", "Equipement du ménage", "niveau_equipement", false, List.of()),
            new Onglet("eclairage", "source d'eclairage du ménage", "acces_eclairage", false, List.of()),
            new Onglet("activite-pro", "Activite professionnelle lié au ménage",
                    "activite_professionnelle", false, List.of()),
            new Onglet("actifs", "Actifs lié au ménage", "autres_actifs", false, List.of()),
            new Onglet("avis", "Avis sur projet", "avis_projet", false, List.of()),
            new Onglet("photo", "Photo", "photo_new", false, List.of()),
            new Onglet("equipement-statut", "Statut de l'équipement", "equipements_important", false, List.of()),
            new Onglet("commerciale", "Activité commerciale", "activites_commerciales_declaree", false, List.of()),
            new Onglet("artisanale", "Activite artisanale", "activite_artisanale_formelle", false, List.of()),
            new Onglet("bien", "Information sur la nature du bien",
                    "caracteristiques_bien_foncier", false, List.of()));

    /** Identifiants SQL surs : lettres, chiffres et tirets bas uniquement. */
    private static final Pattern IDENTIFIANT_SQL = Pattern.compile("[a-z_][a-z0-9_]*");

    private final ObjectMapper json = new ObjectMapper();
    private final Map<String, Onglet> parCode = new LinkedHashMap<>();

    @PostConstruct
    void charger() throws IOException {
        for (Onglet modele : DEFINITIONS) {
            List<Colonne> colonnes = lireColonnes(modele.table());
            for (Colonne colonne : colonnes) {
                if (!IDENTIFIANT_SQL.matcher(colonne.nom()).matches()) {
                    throw new IllegalStateException(
                            "Nom de colonne inattendu dans " + modele.table() + " : " + colonne.nom());
                }
            }
            if (!IDENTIFIANT_SQL.matcher(modele.table()).matches()) {
                throw new IllegalStateException("Nom de table inattendu : " + modele.table());
            }
            parCode.put(modele.code(), new Onglet(modele.code(), modele.libelle(),
                    modele.table(), modele.multiple(), colonnes));
        }
    }

    private List<Colonne> lireColonnes(String table) throws IOException {
        ClassPathResource fichier = new ClassPathResource("entites/" + table + ".json");
        if (!fichier.exists()) {
            throw new IllegalStateException("Metadonnees absentes pour la table " + table);
        }
        try (InputStream flux = fichier.getInputStream()) {
            return json.readValue(flux, new TypeReference<List<Colonne>>() {});
        }
    }

    public List<Onglet> onglets() {
        return List.copyOf(parCode.values());
    }

    /** Onglet par son code, ou {@code null} si le code est inconnu. */
    public Onglet onglet(String code) {
        return parCode.get(code);
    }
}
