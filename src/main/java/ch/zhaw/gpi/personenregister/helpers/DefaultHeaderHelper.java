package ch.zhaw.gpi.personenregister.helpers;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0058._5.SendingApplicationType;
import org.springframework.stereotype.Component;

/**
 * Initialisiert ein HeaderType-Objekt als Bean mit sinnvollen Standard-Eigenschaften
 * Hierdurch können mehrfach verwendete Eigenschaften wie z.B. die Bezeichnung
 * dieser Applikation einmalig gesetzt werden.
 */
@Component
public class DefaultHeaderHelper {
    // HeaderType-Variable deklarieren
    HeaderType headerResponse;
            
    /**
     * Konstruktor
     */
    private DefaultHeaderHelper() {        
        // Initialisieren des neuen HeaderType-Objekts
        headerResponse = new HeaderType();
    
        // Identifikation des Senders gemäss Sedex wird gesetzt (frei erfunden)
        headerResponse.setSenderId("sedex://personenregister");
        
        // Ein neues Objekt mit Eigenschaften zur Personenregister-Applikation wird erstellt
        SendingApplicationType sendingApplication = new SendingApplicationType();
        
        // Diesem Objekt wird der Hersteller/Betreiber zugewiesen
        sendingApplication.setManufacturer("Kanton Bern");
        
        // Diesem Objekt wird die Applikations-Bezeichnung zugewiesen
        sendingApplication.setProduct("Personenregister-Applikation");
        
        // Diesem Objekt wird die Applikations-Version zugewiesen
        sendingApplication.setProductVersion("1.0");
        
        // Dieses Objekt wird dem Antwort-Header zugewiesen
        headerResponse.setSendingApplication(sendingApplication);
        
        // Es wird angegeben, dass es sich um eine Testnachricht handelt (Entwicklungs-Modus) -> in der Realität wäre das ausgelagert z.B. in application.properties
        headerResponse.setTestDeliveryFlag(true);
    }

    /**
     * Gibt ein standardmässiges HeaderType für den Antwort-Header zurück
     * 
     * @param headerRequest     HeaderRequest, um daraus die Nachrichten-Id in den Response zu kopieren
     * @return                  HeaderType-Objekt
     */
    public HeaderType getHeaderResponse(HeaderType headerRequest) {
        // Die im Request erhaltene Nachrichten-Id wird auch in der Response wieder übergeben
        this.headerResponse.setMessageId(headerRequest.getMessageId());
        
        return this.headerResponse;
    }
}
