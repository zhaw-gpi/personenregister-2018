package ch.zhaw.gpi.personenregister.endpoint;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0194._1.DeliveryType;
import ch.ech.xmlns.ech_0194._1.InfoType;
import ch.ech.xmlns.ech_0194._1.NegativeReportType;
import ch.zhaw.gpi.personenregister.controller.ResidentRegisterController;
import ch.zhaw.gpi.personenregister.helpers.DateConversionHelper;
import ch.zhaw.gpi.personenregister.helpers.DefaultHeaderHelper;
import java.math.BigInteger;
import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Endpoint-Definition Hier wird der eigentliche Web Service definert, was zur
 * Laufzeit im Hintergrund dazu führt, dass ein WSDL generiert wird und dass die
 * Web service- Operationen als Methoden bereit gestellt werden. Werden diese
 * Operationen aufgerufen, wird die jeweilige Methode aufgerufen, welche
 * allerdings nur die eigentliche Implementation aufruft, die in einem separaten
 * Controller stattfindet Die @WebService-JAX-WS-Annotation definiert diese
 * Klasse als Web Service-Klasse
 */
@WebService(name = "Personenregister-Service", portName = "PersonenRegisterServicePort", targetNamespace = "http://www.ech.ch/xmlns/eCH-0194/1")
@SchemaValidation
public class PersonenRegisterServiceEndpoint {

    /**
     * Die eigentliche Implementation soll getrennt von der Schnittstellen-
     * Definition sein. Dies wird mit einer separaten Controller-Klasse gelöst.
     * Über @Autowired wird das zur Laufzeit verfügbare Controller-Objekt in
     * diesem Service als Variable eingefügt (Dependency Injection). PS: Damit
     * es zur Laufzeit verfügbar ist, wird es in ApplicationConfiguration.java
     * erstellt
     */
    @Autowired
    private ResidentRegisterController personenRegisterController;
    
    @Autowired
    private DefaultHeaderHelper defaultHeaderHelper;

    /**
     * Definition der Webservice-Operation handleDelivery Diese erwartet das
     * generische Delivery-Objekt und gibt auch ein solches zurück
     * 
     * Prüft, ob wirklich eine personMoveRequest-Nachricht übergeben wird. Falls nein,
     * wird ein negativeReport zurückgegeben. Falls ja, wird die Controller-Klasse
     * aufgerufen, welche ein PersonMoveResponse-Objekt erzeugt. Dieses wird
     * schliesslich gewrapped in einem DeliveryType-Objekt an den
     * Webservice-Endpoint zurück gegeben.
     *
     * Die @WebMethod-JAX-WS-Annotation definiert eine Webservice-Operation
     *
     * @WebParam definiert die Bezeichnung der Nachrichten-Input-Parameter
     * @WebResult definiert die Bezeichnung des Nachrichten-Output-Parameters
     *
     * @param deliveryRequest
     * @return
     */
    @WebMethod(operationName = "handleDelivery")
    @WebResult(name = "delivery")
    public DeliveryType checkResidentOperation(@WebParam(name = "delivery") DeliveryType deliveryRequest) {
        // Header des Anfrage-Objekts in neue Variable auslesen
        HeaderType headerRequest = deliveryRequest.getDeliveryHeader();

        // Ein leeres DeliveryResponse-Objekt instanzieren
        DeliveryType deliveryResponse = new DeliveryType();

        // Header des Antwort-Objekts mit Default-Werten basierend auf Hilfsklasse generieren
        HeaderType headerResponse = defaultHeaderHelper.getHeaderResponse(headerRequest);

        // Ein neues Datumskonversation-Hilfsobjekt wird initialisiert
        DateConversionHelper dateConversionHelper = new DateConversionHelper();

        // Mithilfe dieses Hilfsobjekt wird das aktuelle Datum (inkl. Uhrzeit)
        // in ein XMLGregorianCalendar-Objekt umgewandelt und der Nachrichten-Datums-
        // Eigenschaft im Antwort-Header zugewiesen
        headerResponse.setMessageDate(dateConversionHelper.DateToXMLGregorianCalendar(new Date()));

        // Nur wenn der Nachrichten-Typ der Anfrage einem personMoveRequest entspricht
        // soll es weiter gehen, ansonsten soll ein negativeReport zurück gesendet werden
        if (headerRequest.getMessageType().equals("sedex://personMoveRequest")) {
            // Ruft die eigentliche Implementations-Methode dieser Webservice-Operation auf und weist das Resultat der Webservice-Antwort zu
            deliveryResponse.setPersonMoveResponse(personenRegisterController.handleDelivery(deliveryRequest));

            // Im Header der Antwort wird die Aktion auf 9 (=Positive Antwort) gesetzt
            headerResponse.setAction("9");

            // Im Header der Antwort wird der Nachrichtentyp auf personMoveResponse gesetzt
            headerResponse.setMessageType("sedex://personMoveResponse");

            // Der Header wird der Antwort zugewiesen
            deliveryResponse.setDeliveryHeader(headerResponse);
        } else {
            // Ein neues InfoType-Objekt wird initialisiert
            InfoType info = new InfoType();

            // Diesem Objekt wird der Fehler-Code 1 (Abgelehnt) zugewiesen
            info.setCode(BigInteger.valueOf(1));

            // Diesem Objekt wird eine Fehlermeldung zugewiesen, dass der falsche Nachrichtentyp übergeben wurde
            info.setTextGerman("MessageType sedex://personMoveRequest erwartet, aber "
                    + headerRequest.getMessageType() + " erhalten.");

            // Ein neues NegativeReportType-Objekt wird initialisiert
            NegativeReportType negativeReport = new NegativeReportType();

            // Diesem Objekt wird das InfoType-Objekt als GeneralError-Objekt zugewiesen
            negativeReport.getGeneralError().add(info);

            // Das Objekt wird dem Antwort-Objekt zugewiesen
            deliveryResponse.setNegativeReport(negativeReport);

            // Im Header der Antwort wird die Aktion auf 8 (=Negative Antwort) gesetzt
            headerResponse.setAction("8");

            // Im Header der Antwort wird der Nachrichtentyp auf NegativeReport gesetzt
            headerResponse.setMessageType("sedex://negativeReport");

            // Der Header wird der Antwort zugewiesen
            deliveryResponse.setDeliveryHeader(headerResponse);
        }

        // Die Antwort wird dem Web Service-Client zurück gegeben
        return deliveryResponse;
    }
}
