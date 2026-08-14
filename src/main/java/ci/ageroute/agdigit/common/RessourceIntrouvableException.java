package ci.ageroute.agdigit.common;

/** Levee quand une ressource demandee n'existe pas. Traduite en HTTP 404. */
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
