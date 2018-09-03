# Personenregister 2018 (personenregister-2018)

> Autoren der Dokumentation: Björn Scheppler

> Dokumentation letztmals aktualisiert: 3.9.2018

Dieses Projekt simuliert das kantonale Personenregister, welches Operationen über SOAP unter anderem für die [Umzugsplattform](https://github.com/zhaw-gpi/eumzug-plattform-2018) bereitstellt.

## Wie können Studierende Bonus-Punkte sammeln (in Aufgabenstellung verschieben)
1. Weitere Operationen bereitstellen
2. Eine kleine Webapplikation erstellen als Schnittstelle für CRUD-Operationen
3. Validierung (min-length & Co. für die Requests einbauen)
4. ResidentRegisterController:135:localPersonId sollte frisch pro Person generiert werden und nicht von Hauptperson übernommen werden => ein LocalPersonIdGenerator sollte ein eigener Microservice sein, der sowohl von Umzugsplattform, Personenregister als auch weiteren Services genutzt werden kann

## (Technische) Komponenten
1. **Spring Boot Starter** beinhaltend:
    1. Spring Boot-Standardkonfiguration mit Tomcat als Applikations- und Webserver
    2. Web-Komponenten von Spring
    3. Main-Methode in GwrApplication-Klasse
2. **Persistierungs-Komponenten**:
    1. H2-Datenbank-Unterstützung inklusive Console-Servlet über ApplicationConfiguration-Klasse
    2. Datenmodell (JPA-Diagramm PersonenRegisterModel.jpa)
    3. Java Persistence API (JPA) und persistence.xml
    4. Package Repositories und Package entities für Resident und ResidentRelation
    6. initialData.sql in test/ressources, um die Datenbank initial mit sinnvollen Stammdaten zu füllen
3. **SOAP WebService-Komponenten** (PS: mit Apache CXF gelöst statt nur spring-boot-starter-webservices, da letzteres mit der komplexen Struktur des XSD nicht umgehen kann)
    1. SOAP-Webservice-Komponenten (Java API for XML Web Services-Komponenten,  CXF-Servlet-Komponenten)
    2. WebServiceConfiguration-Klasse
    3. XML-Schema Definition als Basis für das WSDL und die durch ein Maven-Plugin generierten Klassen (eCH-0194-1-0.xsd)
    4. Schnittstellen-Definition und Aufbereitung der webservice-spezifischen Antwort (Endpoint: PersonenRegisterServiceEndpoint)
    5. Hilfsklassen DefaultHeaderHelper und DateConversionHelper
4. **Business-Logik**:
    1. PersonenRegisterController:
        1. Hauptmethode handleDelivery, welche mit der Datenbank kommuniziert und das Ergebnis im Hinblick auf die Webservice-Antwort auswertet
        2. Hilfsmethode populateRelatedPerson, um mitumziehende Personenen für die Webservice-Antwort aufzubereiten
5. "Sinnvolle" **Grundkonfiguration** in application.properties für Tomcat und Datenbank
6. **Test-Fälle** als soapUI-Projekt (PersonenRegisterServiceTests-soapui-project.xml)

## Deployment und Start
1. **Erstmalig** oder bei Problemen ein **Clean & Build (Netbeans)**, respektive `mvn clean install` (Cmd) durchführen
2. Bei Änderungen am POM-File oder bei **(Neu)kompilierungsbedarf** genügt ein **Build (Netbeans)**, respektive `mvn install`
3. Für den **Start** ist ein **Run (Netbeans)**, respektive `java -jar .\target\NAME DES JAR-FILES.jar` (Cmd) erforderlich. Dabei wird Tomcat gestartet, die Datenbank erstellt/hochgefahren mit den Eigenschaften (application.properties) und die verschiedenen Resourcen-URL-Mappings vorgenommen.
4. Initial sind **Daten in der Datenbank anzulegen**. Hierzu an der Console anmelden (http://localhost:8083/console; jdbc:h2:./personenregister, Benutzer sa, kein Passwort) und die SQL-Befehle in test/resources/initalData.sql ausführen.
5. Das **Beenden** geschieht mit **Stop Build/Run (Netbeans)**, respektive **CTRL+C** (Cmd)


## Nutzung der Applikation
### Tests mit soapUI
1. Starten wie im vorherigen Kapitel beschrieben, damit der SOAP WebService läuft und über die URL http://localhost:8083/soap verfügbar ist
2. Testen über folgende Varianten:
    1. In soapUI händisch neue SOAP-Requests generieren mit der WSDL http://localhost:8083/soap/PersonenRegisterService?wsdl. Welche Personen dabei gefunden werden können, kann über die H2-Konsole geprüft werden in den Tabellen RESIDENT und RESIDENT_RELATION. Hierzu auf http://localhost:8083/console anmelden mit Driver Class = org.h2.Driver, JDBC URL = jdbc:h2:./personenregister, User Name = sa, Password = Leer lassen
    2. Die vorgefertigten Requests/TestSuite nutzen im soapUI-Projekt \src\test\resources\PersonenRegisterServiceTests-soapui-project.xml

### Tests aus der Umzugsplattform heraus
Hierzu den Anweisungen folgen in https://github.com/zhaw-gpi/eumzug-plattform-2018

## Prototypische Vereinfachungen
1. In personMoveRequest werden die municipalityId und municipalityName vermutlich deshalb mitgeliefert, weil eine Person in mehreren Gemeinden registriert sein kann (Hauptwohnsitz und Wochenaufenthalter). Das heisst, moveAllowed müsste eigentlich bei der Beziehungstabelle sein zwischen Municipality und Resident. Dies mit JPA umzusetzen, ist gar nicht so einfach, u.a. braucht es dann eine eigene Entität für diese Beziehungstabelle mit einem Pseudo-Primärschlüssel: https://www.thoughts-on-java.org/many-relationships-additional-properties/. Daher lassen wir dies weg.
2. Schema Validation: Mit @SchemaValidation bei der Webservice-Endpoint-Klasse werden lediglich Standard-Fehlermeldungen ausgegeben, nicht aber benutzerdefinierte.
Das ist ok bei einem Prototyp. Störender ist, dass XJC keine Annotations für die XSD-Restrictions hinzufügt (also, dass z.B. ein String nicht leer oder länger als
40 Zeichen sein darf). Es gibt teilweise Plugins, aber die sind seit mehreren Jahren nicht mehr aktualisiert => man müsste entweder ein eigenes Plugin schreiben
oder aber die Annotations in den generierten Java-Klassen von Hand hinzufügen, aber dann wäre die Generierung im Build-Prozess natürlich nicht mehr opportun.

## Mitwirkende
1. Björn Scheppler: Hauptarbeit
2. Peter Heinrich: Der stille Support im Hintergrund mit vielen Tipps sowie zuständig
für den Haupt-Stack mit SpringBoot & Co.
3. Gruppe TZb02 (Bekim Kadrija, Jovica Rajic, Luca Belmonte, Simon Bärtschi, Sven 
Baumann): Mitzuziehende Personen auswählen