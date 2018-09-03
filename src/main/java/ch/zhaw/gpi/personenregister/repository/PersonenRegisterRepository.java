package ch.zhaw.gpi.personenregister.repository;

import ch.zhaw.gpi.personenregister.entities.ResidentEntity;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Repository mit CRUD- und weitern Operationen für Bürger-Objekte (ResidentEntity)
 Erweitert die mit dem Spring Data JPA-Paket mitgelieferte CrudRepository, so
 dass einerseits einfache CRUD-Operationen mit der ResidentEntity-Entität durchgeführt
 werden können als auch eigene definierte Methoden
 */
public interface PersonenRegisterRepository extends CrudRepository<ResidentEntity, Long> {
    
    /**
     * Methode, um alle Personen mit bestimmten Identifikationsmerkmalen zu erhalten
     * Funktioniert basierend auf einer in JPQL (Java Persistence Query Language)
     * geschriebenen Abfrage
     * @param firstName
     * @param officialName
     * @param sex
     * @param dateOfBirth
     * @return 
     */
    @Query("SELECT r FROM Resident r WHERE r.firstName = :firstName AND " +
            "r.officialName = :officialName AND r.sex = :sex AND " +
            "r.dateOfBirth = :dateOfBirth")
    public List<ResidentEntity> findResidentByIdentificationParameters(
            @Param("firstName") String firstName,
            @Param("officialName") String officialName,
            @Param("sex") Integer sex,
            @Param("dateOfBirth") Date dateOfBirth
    );
}
