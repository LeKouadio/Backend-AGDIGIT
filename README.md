# Backend-AGDIGIT

Backend de démonstration de l'ERP AGDIGIT — API REST Spring Boot exposant les
données PAP (Personnes Affectées par le Projet).

Ce service est en **lecture seule**. Les données sont écrites dans PostgreSQL par
le workflow n8n, qui les récupère depuis KoboToolbox. Ce backend ne fait que les
servir au frontend Angular.

```
KoboToolbox ──► n8n ──► PostgreSQL ──► CE BACKEND ──► Frontend Angular
```

## Prérequis

| | Version | Vérifier |
|---|---|---|
| JDK | 21+ | `java -version` |
| Maven | 3.9+ | `mvn -v` |
| PostgreSQL | 14+ | base contenant les tables AGDIGIT |

## Démarrage

### 1. Configurer la connexion à la base

C'est le point à régler en premier : tant que la connexion échoue, rien d'autre
ne fonctionne. Les paramètres se trouvent dans
`src/main/resources/application.properties` et sont surchargeables par variables
d'environnement, pour ne jamais committer de mot de passe.

```powershell
$env:AGDIGIT_DB_HOST = "localhost"
$env:AGDIGIT_DB_PORT = "5432"
$env:AGDIGIT_DB_NAME = "agdigit"
$env:AGDIGIT_DB_USER = "postgres"
$env:AGDIGIT_DB_PASSWORD = "votre_mot_de_passe"
```

### 2. Lancer

```bash
mvn spring-boot:run
```

L'API écoute sur `http://localhost:8080`.

### 3. Tester dans Postman

```
GET  http://localhost:8080/api/paps
GET  http://localhost:8080/api/paps?page=0&size=20&commune=COCODY
GET  http://localhost:8080/api/paps?nom=KOUASSI
GET  http://localhost:8080/api/paps/communes
GET  http://localhost:8080/api/paps/statistiques
GET  http://localhost:8080/api/paps/PVS%2FS1%2FCO%2FDJ1%2FMEN%2F000000367
```

> L'identifiant PAP contient des `/`. Dans une URL il doit être encodé en `%2F`,
> sinon le serveur le lit comme plusieurs segments de chemin.

Tester dans Postman **avant** de développer le frontend : si l'API répond
correctement ici, tout ce qui est en amont (connexion, base, données) fonctionne.
Un écran vide ensuite ne pourra venir que du frontend.

## Structure

```
src/main/java/ci/ageroute/agdigit/
├── AgdigitBackendApplication.java   point d'entrée
├── config/
│   └── ConfigurationCors.java       autorise le front (localhost:4200)
├── common/
│   ├── RessourceIntrouvableException.java
│   └── GestionnaireExceptions.java  traduit les erreurs en JSON
└── pap/
    ├── entity/FichePap.java         table ag_survey_sheet_test (71 colonnes)
    ├── repository/FichePapRepository.java
    ├── service/FichePapService.java
    ├── dto/FichePapResumeDto.java   vue allégée pour les listes
    └── controller/FichePapController.java
```

### Ajouter une nouvelle table

Le paquet `pap` est le patron à dupliquer. Pour chacune des 11 autres entités
AGDIGIT (`is_chef`, `acces_eclairage`, `caracteristiques_bien_foncier`,
`activite_professionnelle`, `autres_actifs`, `avis_projet`, `niveau_equipement`,
`equipements_important`, `activites_commerciales_declaree`,
`activite_artisanale_formelle`, `photo_new`) :

1. `entity/` — une classe `@Entity` avec `@Table(name = "...")`, un champ par
   colonne annoté `@Column(name = "...")`.
2. `repository/` — une interface qui étend `JpaRepository`.
3. `service/` — les règles métier et la conversion en DTO.
4. `controller/` — les routes HTTP.

Toutes ces tables se rattachent à la fiche principale par `identifiant_pap`.
`is_chef` (les membres du ménage) a une particularité : sa clé est le couple
`identifiant_pap` + `nom_membre_men`, à déclarer avec `@IdClass` ou `@EmbeddedId`.

## Points d'attention sur le schéma

- **Aucune écriture.** `spring.jpa.hibernate.ddl-auto=none` : Hibernate ne crée
  ni ne modifie aucune table. Ne jamais passer à `update` ou `create` sur cette
  base, le schéma appartient à la plateforme AGDIGIT.
- **Colonnes `numeric` contenant du texte.** `commune`, `niveau_instruc` et
  `app_culture` sont déclarées `numeric` dans les entités AGDIGIT mais reçoivent
  des libellés (`BINGERVILLE`, `Analphabète`, `Kwa Lagunaire`). Elles sont
  mappées en `String` ici. Si l'insertion échoue côté n8n, c'est de là que ça
  vient.
- **Clé primaire.** L'entité utilise `identifiant_pap` comme `@Id` : c'est la clé
  d'upsert du pipeline, donc unique. Si votre table physique possède en plus une
  clé technique `id`, déplacez l'annotation.

## Environnement : antivirus qui inspecte le TLS

Si `mvn` échoue avec `PKIX path building failed` ou `Tag mismatch`, c'est un
antivirus qui intercepte les connexions HTTPS (Avast, Kaspersky, ESET…). Deux
réglages, à combiner :

```bash
# 1. Faire confiance au certificat racine de l'antivirus
#    (copie du cacerts du JDK + le certificat, sans modifier le JDK)
keytool -importcert -trustcacerts -alias antivirus \
        -file racine-antivirus.pem \
        -keystore cacerts-local.jks -storepass changeit

# 2. Lancer Maven avec ce magasin ET en TLS 1.2
#    (« Tag mismatch » vient du chiffrement GCM de TLS 1.3 mal géré par l'antivirus)
export MAVEN_OPTS="-Djavax.net.ssl.trustStore=/chemin/cacerts-local.jks \
                   -Djavax.net.ssl.trustStorePassword=changeit \
                   -Dhttps.protocols=TLSv1.2 -Djdk.tls.client.protocols=TLSv1.2"
```

La solution durable reste de désactiver l'analyse HTTPS de l'antivirus, ou d'y
ajouter une exclusion pour `repo.maven.apache.org` et `registry.npmjs.org`.

## Tests

```bash
mvn test
```

Les tests utilisent une base H2 en mémoire : ils passent sans PostgreSQL
installé, ce qui permet de vérifier que le contexte Spring se charge.
