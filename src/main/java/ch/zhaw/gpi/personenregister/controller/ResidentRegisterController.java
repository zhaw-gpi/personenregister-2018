package ch.zhaw.gpi.personenregister.controller;

import ch.ech.xmlns.ech_0044._4.DatePartiallyKnownType;
import ch.ech.xmlns.ech_0044._4.NamedPersonIdType;
import ch.ech.xmlns.ech_0044._4.PersonIdentificationType;
import ch.ech.xmlns.ech_0194._1.DeliveryType;
import ch.ech.xmlns.ech_0194._1.PersonMoveResponse;
import ch.zhaw.gpi.personenregister.entities.ResidentEntity;
import ch.zhaw.gpi.personenregister.helpers.DateConversionHelper;
import java.math.BigInteger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ch.zhaw.gpi.personenregister.repository.ResidentRepository;

/**
 * Implementation für den PersonenRegisterService Diese Klasse enthält die
 * eigentliche Implementation der Web Service-Operationen, wobei jede Funktion
 * einer gleich lautenden Web Service-Operation entspricht. Sie stellt die
 * Verbindung zwischen Web Service-Schnittstelle und Process Engine her
 */
@Component
public class ResidentRegisterController {

    // Das für die Datenbankabfragen zuständige Repository wird als Dependency injiziert
    @Autowired
    private ResidentRepository residentRepository;

    /**
     * Implementation der Web Service-Operation handleDelivery
     * 
     * Enthält die eigentliche Business-Logik, welche darin besteht, in der Datenbank
     * über das PersonenRegisterRepository die umzuziehende Person zu suchen. Existiert
     * sie nicht, wird PersonKnown auf false gesetzt. Existiert sie, wird geprüft, ob
     * sie die Umzugsmeldung elektronisch melden darf (moveAllowed). Falls nicht,
     * geht die Antwort so zurück. Darf sie es, werden die Eigenschaften aus der Entität
     * in das eCH-Format übersetzt. Zudem werden allenfalls mitumziehende Personen
     * ebenfalls aufbereitet.
     *
     * @param deliveryRequest   Die Anfrage als DeliveryType-eCH-Objekt
     * @return                  Die Person inklusive mitumziehender Personen als PersonMoveResponse-eCH-Objekt
     */
    public PersonMoveResponse handleDelivery(DeliveryType deliveryRequest) {
        /**
         * Antwort-Objekt (PersonMoveResponse) vorbereiten
         */
        
        // Ein neues Antwort-Teil-Objekt wird instanziert
        PersonMoveResponse personMoveResponse = new PersonMoveResponse();

        // PersonIdentification des Anfrage-Objekts in neue Variable auslesen
        PersonIdentificationType personIdentification = deliveryRequest.getPersonMoveRequest().getPersonIdentification();

        // Dem Antwort-Teil-Objekt wird die in der Anfrage erhaltene Personenidentifikation übergeben
        personMoveResponse.setPersonIdentification(personIdentification);

        // Dem Antwort-Teil-Objekt wird die in der Anfrage erhaltene Gemeindeidentifikation übergeben
        personMoveResponse.setReportingMunicipality(deliveryRequest.getPersonMoveRequest().getMunicipality());

        /**
         * Person und mitumziehende Personen in Datenbank suchen
         */
        
        // Repository-Methode aufrufen, welche die eigentliche Datenbankabfrage basierend auf dem Request durchführt und ein personsFound-Objekt zurückgibt
        List<ResidentEntity> personsFound = residentRepository.findByFirstNameAndOfficialNameAndSexAndDateOfBirth(
                personIdentification.getFirstName(),
                personIdentification.getOfficialName(),
                Integer.parseInt(personIdentification.getSex()),
                personIdentification.getDateOfBirth().getYearMonthDay().toGregorianCalendar().getTime()
        );

        /**
         * PersonKnown, moveAllowed und mitumziehende Personen festlegen
         */
        
        // Wenn keine oder mehr als eine Person gefunden wurde
        if (personsFound.size() != 1) {
            // PersonKnown auf false setzen
            personMoveResponse.setPersonKnown(false);
        } else {
            // Ansonsten PersonKnown auf true setzen
            personMoveResponse.setPersonKnown(true);

            // Diese eine Person als Variable speichern
            ResidentEntity resident = personsFound.get(0);

            // Die Boolean-Variable für moveAllowed (Umzugsmeldung elektronisch erlaubt) muss in eine Integer-Variable umgewandelt werden
            BigInteger moveAllowedInteger = (resident.isMoveAllowed() ? BigInteger.valueOf(1) : BigInteger.valueOf(2));

            // Die Integer-Variable kann dem Antwort-Objekt übergeben werden
            personMoveResponse.setMoveAllowed(moveAllowedInteger);
            
            // Mitumziehende Personen ermitteln, falls die Person elektronisch den Umzug melden darf
            if (resident.isMoveAllowed()) {
                // Alle Personen im gleichen Haushalt auslesen
                if(resident.getResidentRelationEntity() != null){
                    List<ResidentEntity> relatives = resident.getResidentRelationEntity().getResidentEntities();
                    
                    // Aus dieser Liste die eigene Person entfernen
                    if(relatives != null){
                        ResidentEntity toRemoveRelative = null;
                        for(ResidentEntity relative : relatives){
                            if(relative.getPersonId().equals(resident.getPersonId())){
                                toRemoveRelative = relative;                            
                            }
                        }
                        relatives.remove(toRemoveRelative);
                    }

                    // Wenn es nun noch weitere Personen gibt ...
                    if (relatives != null && !relatives.isEmpty()) {
                        // ... jedes Element der Liste wird einer Variable vom Typ ResidentEntity zugewiesen. 
                        for (ResidentEntity relative : relatives) {
                            // Hilfsmethode populateRelatedPerson aufrufen, um alle Angaben der Entity dem passenden Antwort-Objekt zuzuweisen
                            PersonMoveResponse.RelatedPerson relatedPerson = this.populateRelatedPerson(relative, personIdentification.getLocalPersonId());

                            // Die mitzuziehende Personen werden dem Antwort-Objekt übergeben, falls sie nicht null sind
                            if(relatedPerson != null){
                                personMoveResponse.getRelatedPerson().add(relatedPerson);
                            }
                        }
                    }
                }
            }
        }

        // Das personMoveResponse-Objekt wird zurück gegeben
        return personMoveResponse;
    }

    /**
     * Hilfsmethode, um alle Angaben der Entity dem passenden Antwort-Objekt zuzuweisen, sofern die mitumziehende Person umziehen darf
     * 
     * @param resident                  Die mitumziehende Person aus der Datenbank
     * @param localPersonIdMainPerson   Die localPersonId der Hauptperson
     * @return                          Eine mitumziehende Person in der passenden eCH-Form
     */
    private PersonMoveResponse.RelatedPerson populateRelatedPerson(ResidentEntity resident, NamedPersonIdType localPersonIdMainPerson) {
        // Nur Personen, welche umziehen dürfen, werden der Liste hinzugefügt
        if (resident.isMoveAllowed()) {
            // Related Person wird instanziert. RelatedPerson entspricht letztendlich einer mitzuziehenden Person.
            PersonMoveResponse.RelatedPerson relatedPerson = new PersonMoveResponse.RelatedPerson();

            // PersonIdentificationType wird für die mitzuziehenden Person instanziert.
            PersonIdentificationType relatedPersonIdentification = new PersonIdentificationType();

            // Vorhandene LocalPersonID der Hauptperson wird für jede mitzuziehende Person zugewiesen.
            relatedPersonIdentification.setLocalPersonId(localPersonIdMainPerson);

            // Vorname der mitzuziehenden Person wird gesetzt.
            relatedPersonIdentification.setFirstName(resident.getFirstName());

            // Nachname der mitzuziehenden Person wird gesetzt.
            relatedPersonIdentification.setOfficialName(resident.getOfficialName());

            // Ein neues Datumskonversation-Hilfsobjekt wird initialisiert
            DateConversionHelper dateConversionHelper = new DateConversionHelper();

            // DatePartiallyKnownType wird instanziert. Wird gebraucht um Geburtstagsdatum der mitzuziehenden Person zu setzen.
            DatePartiallyKnownType relatedPersonDateOfBirth = new DatePartiallyKnownType();

            // Das Geburtstagsdatum wird im Format YearMonthDay gesetzt. Es wird die Methode dateToXMLGregorianCalendar in der Klasse DateConversionHelper aufgerufen, 
            // um das Datum in ein XMLGregorianCalendar umzuwandeln.
            relatedPersonDateOfBirth.setYearMonthDay(dateConversionHelper.DateToXMLGregorianCalendar(resident.getDateOfBirth()));

            // Geburtstagsdatum der mitzuziehenden Person wird gesetzt.
            relatedPersonIdentification.setDateOfBirth(relatedPersonDateOfBirth);

            // Geschlecht der mitzuziehenden Person wird gesetzt. Da ein String erwartet wird, wird Variable sex von int in ein String umgewandelt.
            relatedPersonIdentification.setSex(Integer.toString(resident.getSex()));

            // Die Personen Identifikation wird der mitzuziehenden Person gesetzt.
            relatedPerson.setPersonIdentification(relatedPersonIdentification);

            // Die mitumziehende Person zurückgeben
            return relatedPerson;
        } else {
            // null zurückgeben
            return null;
        }
    }
}
