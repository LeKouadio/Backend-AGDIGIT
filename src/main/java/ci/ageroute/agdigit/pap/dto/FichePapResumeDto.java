package ci.ageroute.agdigit.pap.dto;

import ci.ageroute.agdigit.pap.entity.FichePap;

/**
 * Vue allegee d'une fiche PAP, pour les ecrans de liste.
 *
 * <p>On n'expose pas l'entite directement : elle porte 78 colonnes, dont des
 * colonnes techniques qui n'ont rien a faire dans une reponse HTTP. Le DTO
 * fixe le contrat de l'API et permet de faire evoluer la base sans casser le
 * frontend.
 *
 * <p>Un {@code record} Java suffit : immuable, serialise en JSON tel quel.
 */
public record FichePapResumeDto(
        String identifiantPap,
        String nomPap,
        String genre,
        String commune,
        String quartier,
        String dateEnquete,
        String enqueteur,
        String telephone1,
        String situationMat,
        String nationalite) {

    public static FichePapResumeDto depuis(FichePap fiche) {
        return new FichePapResumeDto(
                fiche.getIdentifiantPap(),
                fiche.getNomPap(),
                fiche.getGenre(),
                fiche.getCommune(),
                fiche.getQuartier(),
                fiche.getDateEnquete() != null ? fiche.getDateEnquete().toString() : null,
                fiche.getEnqueteur(),
                fiche.getTelephone1(),
                fiche.getSituationMat(),
                fiche.getNationalite());
    }
}
