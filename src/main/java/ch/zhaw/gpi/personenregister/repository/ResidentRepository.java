package ch.zhaw.gpi.personenregister.repository;

import ch.zhaw.gpi.personenregister.entities.ResidentEntity;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Einwohner-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface ResidentRepository extends JpaRepository<ResidentEntity, Long> {
    
    /**
     * Methode, um alle Personen mit bestimmten Identifikationsmerkmalen zu erhalten
     * 
     * @param firstName     Vorname
     * @param officialName  Nachname
     * @param sex           Geschlecht
     * @param dateOfBirth   Geburtsdatum
     * @return              Liste aller Personen mit zutreffenden Kriterien (normalerweise eine oder null)
     */
    public List<ResidentEntity> findByFirstNameAndOfficialNameAndSexAndDateOfBirth(
            String firstName,
            String officialName,
            Integer sex,
            Date dateOfBirth
    );
}
