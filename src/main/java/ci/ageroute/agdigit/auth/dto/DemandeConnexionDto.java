package ci.ageroute.agdigit.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Corps de POST /api/auth/connexion. */
public record DemandeConnexionDto(
        @NotBlank(message = "L'identifiant est obligatoire") String identifiant,
        @NotBlank(message = "Le mot de passe est obligatoire") String motDePasse) {}
