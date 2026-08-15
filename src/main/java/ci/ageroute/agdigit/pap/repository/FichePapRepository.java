package ci.ageroute.agdigit.pap.repository;

import ci.ageroute.agdigit.pap.entity.FichePap;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Acces aux fiches PAP.
 *
 * <p>Spring Data implemente cette interface au demarrage : il n'y a aucune classe
 * a ecrire. Les methodes dont le nom respecte la convention
 * {@code findBy<Propriete><Operateur>} sont traduites automatiquement en SQL.
 */
@Repository
public interface FichePapRepository extends JpaRepository<FichePap, String> {

    Page<FichePap> findByIdentifiantPapContainingIgnoreCase(String fragment, Pageable pageable);

    Page<FichePap> findByNomPapContainingIgnoreCase(String fragment, Pageable pageable);

    Page<FichePap> findByCommuneContainingIgnoreCase(String fragment, Pageable pageable);

    Page<FichePap> findByQuartierContainingIgnoreCase(String fragment, Pageable pageable);

    Page<FichePap> findByEnqueteurContainingIgnoreCase(String fragment, Pageable pageable);

    Page<FichePap> findByNumPieceContainingIgnoreCase(String fragment, Pageable pageable);

    Page<FichePap> findByTelephone1ContainingIgnoreCase(String fragment, Pageable pageable);

    /** Liste des communes presentes, pour alimenter un filtre cote frontend. */
    @Query("select distinct f.commune from FichePap f where f.commune is not null order by f.commune")
    List<String> listerCommunes();

    /** Nombre de fiches par commune, pour un indicateur de tableau de bord. */
    @Query("""
            select f.commune as commune, count(f) as total
            from FichePap f
            where f.commune is not null
            group by f.commune
            order by count(f) desc
            """)
    List<Object[]> compterParCommune();
}
