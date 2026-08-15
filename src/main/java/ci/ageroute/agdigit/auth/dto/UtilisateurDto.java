package ci.ageroute.agdigit.auth.dto;

/** Utilisateur tel qu'expose au frontend. Ne contient jamais le mot de passe. */
public record UtilisateurDto(String identifiant, String nomComplet, String role) {}
