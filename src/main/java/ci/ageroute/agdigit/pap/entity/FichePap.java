package ci.ageroute.agdigit.pap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fiche principale d'une Personne Affectee par le Projet.
 *
 * <p>Correspond a la table {@code ag_survey_sheet_test}, alimentee par le workflow
 * n8n a partir des soumissions KoboToolbox. Les noms de colonnes reprennent le
 * chemin du groupe dans le formulaire, les {@code /} remplaces par des {@code _}
 * (ex. {@code identification_enquete/email} devient {@code identification_enquete_email}).
 *
 * <p>La cle metier est {@code identifiant_pap} : c'est sur elle que porte l'upsert
 * du pipeline, elle est donc unique. Si votre table physique possede en plus une
 * cle technique {@code id}, deplacez l'annotation {@code @Id} dessus.
 *
 * <p>Entite en LECTURE SEULE pour cette demo : l'ecriture est faite par n8n.
 */
@Entity
@Table(name = "ag_survey_sheet_test")
@Getter
@Setter
@NoArgsConstructor
public class FichePap {

    /** Identifiant metier de la PAP, ex. PVS/S1/CO/DJ1/MEN/000000367. */
    @Id
    @Column(name = "identifiant_pap", nullable = false)
    private String identifiantPap;

    /** Précision sur une autre organisation */
    @Column(name = "a_compo_men_autre_organisation")
    private String aCompoMenAutreOrganisation;

    /** présence d'enfants vulnérables dans le ménage */
    @Column(name = "a_compo_men_enf_vul_menage")
    private String aCompoMenEnfVulMenage;

    /** présence d'une femme cheffe de ménage */
    @Column(name = "a_compo_men_fem_chef_menage")
    private String aCompoMenFemChefMenage;

    /** présence de femmes enceintes dans le ménage */
    @Column(name = "a_compo_men_fem_grossess_menage")
    private String aCompoMenFemGrossessMenage;

    /** présence de personnes ayant un handicap physique dans le ménage */
    @Column(name = "a_compo_men_handicapephysment_menage")
    private String aCompoMenHandicapephysmentMenage;

    /** nombre total de personnes vulnérables dans le ménage */
    @Column(name = "a_compo_men_nbre_pers_vuln_menage")
    private String aCompoMenNbrePersVulnMenage;

    /** présence d'orphelin enceintes dans le ménage */
    @Column(name = "a_compo_men_orphelin_menage")
    private String aCompoMenOrphelinMenage;

    /** présence de personnes âgées vulnérables dans le ménage */
    @Column(name = "a_compo_men_pers_agee_vul_menage")
    private String aCompoMenPersAgeeVulMenage;

    /** présence de personnes atteintes d'une maladie mentale dans le ménage */
    @Column(name = "a_compo_men_pers_maladie_meta_menage")
    private String aCompoMenPersMaladieMetaMenage;

    /** Précision d'une autre activité principale exercée */
    @Column(name = "activite_professionnelle_autre_activite_princ")
    private String activiteProfessionnelleAutreActivitePrinc;

    /** Précision sur un autre lieu de travail */
    @Column(name = "activite_professionnelle_autre_lieu_travail")
    private String activiteProfessionnelleAutreLieuTravail;

    /** Montant total des transferts réalisés ou reçus au cours des six derniers mois */
    @Column(name = "activite_professionnelle_montant_transfer_6mois")
    private String activiteProfessionnelleMontantTransfer6mois;

    /** Nombre de transferts liés à l'activité professionnelle */
    @Column(name = "activite_professionnelle_nbr_transfer")
    private String activiteProfessionnelleNbrTransfer;

    /** Superficie totale de la parcelle utilisée pour l'activité professionnelle. */
    @Column(name = "activite_professionnelle_sup_totale_parcelle")
    private String activiteProfessionnelleSupTotaleParcelle;

    /** Année d'installation */
    @Column(name = "annee_inst")
    private String anneeInst;

    /** Groupe ethnique */
    @Column(name = "app_culture")
    private String appCulture;  // colonne declaree `numeric` en base mais alimentee en texte

    /** Appartenance à une organisation */
    @Column(name = "appartenance_org")
    private String appartenanceOrg;

    /** Avantage de l'association de l'organisation */
    @Column(name = "avantage_assoc_org")
    private String avantageAssocOrg;

    /** Commune de l'enquêté */
    @Column(name = "commune")
    private String commune;  // colonne declaree `numeric` en base mais alimentee en texte

    /** Code de la commune */
    @Column(name = "commune_code")
    private String communeCode;

    /** année d'utilisation d'eau */
    @Column(name = "cons_alim_acces_eau_annee_util_eau")
    private String consAlimAccesEauAnneeUtilEau;

    /** distance moyenne des domiciles à un point d'eau */
    @Column(name = "cons_alim_acces_eau_dist_moy_dom_eau")
    private String consAlimAccesEauDistMoyDomEau;

    /** montant des dépenses liées à l'eau */
    @Column(name = "cons_alim_acces_eau_montant_dep_eau")
    private String consAlimAccesEauMontantDepEau;

    /** source combustion */
    @Column(name = "cons_alim_acces_eau_src_combustion")
    private String consAlimAccesEauSrcCombustion;

    /** Source d'accès à l'eau */
    @Column(name = "cons_alim_acces_eau_src_eau")
    private String consAlimAccesEauSrcEau;

    /** Numéro de l'enquêteur */
    @Column(name = "contact")
    private String contact;

    /** Montant total des charges supportées par le ménage */
    @Column(name = "cout_total_charge_menage")
    private String coutTotalChargeMenage;

    /** Date de l'enquête */
    @Column(name = "date_enquete")
    private LocalDate dateEnquete;

    /** Date de naissance */
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    /** Déclaration organisation du quartier */
    @Column(name = "declaration_org_quartier")
    private String declarationOrgQuartier;

    /** Nom de l'enquêteur */
    @Column(name = "enqueteur")
    private String enqueteur;

    /** Ethnie */
    @Column(name = "ethnie")
    private String ethnie;

    /** Genre */
    @Column(name = "genre")
    private String genre;

    /** Identification du pap */
    @Column(name = "id_pap")
    private String idPap;

    /** Précision sur une autre ethnie */
    @Column(name = "identification_enquete_autre_ethnie")
    private String identificationEnqueteAutreEthnie;

    /** précision sur le lieu de résidence autre que ceux proposés dans la liste */
    @Column(name = "identification_enquete_autre_lieu_resid")
    private String identificationEnqueteAutreLieuResid;

    /** Précision sur un autre motif d'installation */
    @Column(name = "identification_enquete_autre_motif_install")
    private String identificationEnqueteAutreMotifInstall;

    /** Précision sur une autre nature de pièce */
    @Column(name = "identification_enquete_autre_nature_piece")
    private String identificationEnqueteAutreNaturePiece;

    /** Adresse email */
    @Column(name = "identification_enquete_email")
    private String identificationEnqueteEmail;

    /** Est-il le chef de ménage? */
    @Column(name = "is_chef_men")
    private Boolean isChefMen;

    /** sf */
    @Column(name = "is_personne_vul_menage")
    private String isPersonneVulMenage;

    /** Lieu de résidence de l'activité */
    @Column(name = "lieu_residence_act")
    private String lieuResidenceAct;

    /** activité secondaire exercée par le ménage en complément de l'activité principale */
    @Column(name = "list_activi_declarati_activi_activite_second_menage")
    private String listActiviDeclaratiActiviActiviteSecondMenage;

    /** Lieu où l'activité secondaire est exercée */
    @Column(name = "list_activi_declarati_activi_lieu_activite_sec")
    private String listActiviDeclaratiActiviLieuActiviteSec;

    /** l'année d'installation sur le site */
    @Column(name = "liste_categorie_annee_install_site")
    private String listeCategorieAnneeInstallSite;

    /** catégorie */
    @Column(name = "liste_categorie_categorie")
    private String listeCategorieCategorie;

    /** lieu principal d'approvisionnement */
    @Column(name = "liste_categorie_lieu_princ_approvi")
    private String listeCategorieLieuPrincApprovi;

    /** origine du client */
    @Column(name = "liste_categorie_origine_client")
    private String listeCategorieOrigineClient;

    /** Montant total des salaires versés aux employés */
    @Column(name = "liste_categorie_total_sal_emp")
    private String listeCategorieTotalSalEmp;

    /** Ancien identifiant de l'enregistrement */
    @Column(name = "meta_deprecated_id")
    private String metaDeprecatedId;

    /** Motif d'installation */
    @Column(name = "motif_install")
    private String motifInstall;

    /** Nationalité */
    @Column(name = "nationalite")
    private String nationalite;

    /** Nature de la pièce */
    @Column(name = "nature_piece")
    private String naturePiece;

    /** Nombre de personnes du ménage */
    @Column(name = "nbre_personnes_menage")
    private Integer nbrePersonnesMenage;

    /** Niveau d'instruction */
    @Column(name = "niveau_instruc")
    private String niveauInstruc;  // colonne declaree `numeric` en base mais alimentee en texte

    /** Nom du pap */
    @Column(name = "nom_pap")
    private String nomPap;

    /** Nom de la personne à contacter */
    @Column(name = "nom_prenom_pers_contacter")
    private String nomPrenomPersContacter;

    /** Nom du propriétaire */
    @Column(name = "nom_proprio")
    private String nomProprio;

    /** Numéro de la pièce */
    @Column(name = "num_piece")
    private String numPiece;

    /** Code du projet */
    @Column(name = "projet_code")
    private String projetCode;

    /** Quartier de l'enquêté */
    @Column(name = "quartier")
    private String quartier;

    /** Code du quartier */
    @Column(name = "quartier_code")
    private String quartierCode;

    /** Section du code */
    @Column(name = "section_code")
    private String sectionCode;

    /** Situation matrimoniale */
    @Column(name = "situation_mat")
    private String situationMat;

    /** Statut de l'association */
    @Column(name = "statut_association")
    private String statutAssociation;

    /** Numéro de téléphone principal */
    @Column(name = "telephone1")
    private String telephone1;

    /** Numéro de téléphone secondaire */
    @Column(name = "telephone2")
    private String telephone2;

    /** Téléphone du ménage */
    @Column(name = "telephone_men")
    private String telephoneMen;

    /** Numéro de téléphone de la personne à contacter */
    @Column(name = "telephone_pers_contacter")
    private String telephonePersContacter;

    /** Type de relation des membres de l'organisation */
    @Column(name = "type_rela__mem_org")
    private String typeRelaMemOrg;

    // ------------------------------------------------------------------ //
    // Colonnes techniques ajoutees par la plateforme AGDIGIT
    // ------------------------------------------------------------------ //

    @Column(name = "__createdat")
    private Instant createdat;

    @Column(name = "__createdby")
    private String createdby;

    @Column(name = "__interimby")
    private String interimby;

    @Column(name = "__site")
    private String site;

    @Column(name = "__status")
    private String status;

    @Column(name = "__updatedat")
    private Instant updatedat;

    @Column(name = "__updatedby")
    private String updatedby;
}
