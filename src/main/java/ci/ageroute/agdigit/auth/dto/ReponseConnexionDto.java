package ci.ageroute.agdigit.auth.dto;

/** Reponse a une connexion reussie. */
public record ReponseConnexionDto(String jeton, long expireDansSecondes, UtilisateurDto utilisateur) {}
