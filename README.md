Björn Scheppler, 10.08.2018

# Personenregister (resident-register)
Dieses Maven-Projekt basierend auf Camunda Spring Boot Starter simuliert eine mögliche Lösung 
für einen Ausschnitt aus dem Personenregister-System, welches im Rahmen der Modulabschlussarbeit
für die eUmzug-Plattform benötigt wird.

Enthalten sind folgende Komponenten/Funktionalitäten:
1. Spring Boot 2.0.2 konfiguriert für Tomcat
2. Camunda Spring Boot Starter 3.0.0
3. Camunda Process Engine, REST API und Webapps (Tasklist, Cockpit, Admin) in der Version 7.9.2 (Enterprise Edition)
4. H2-Datenbank-Unterstützung (von Camunda Engine benötigt)
5. SOAP-Webservice-Komponenten (Java API for XML Web Services-Komponenten,  CXF-Servlet-Komponenten)
6. "Sinnvolle" Grundkonfiguration in application.properties für Camunda, Datenbank und Tomcat
7. Prozessmodell (ResidentRegisterProcesses.bpmn) mit dem Prozess Personenidentifikation
8. Camunda Java Delegate-Klasse (GetResidentFromResidentRegisterDelegate) für den Service Task des Prozesses
9. Datenlayer:
    1. Datenmodell (JPA-Diagramm ResidentRegisterModel.jpa)
    2. zugehörige Entity-Klassen Einwohner (ResidentEntity) und Einwohner-Beziehung (ResidentRelationEntity)
    3. CRUD-Repository-Klasse ResidentRegisterRepository mit der Methode findResidentByIdentificationParameters für die Java Delegate-Klasse,
welcher auf die H2-Datenbank zugreift gemäss application.properties
10. SOAP Web Service-Layer:
    1. WebServiceConfiguration-Klasse
    2. Schnittstellen-Definition (Endpoint: ResidentRegisterServiceEndpoint)
    3. Implementation des Web Services (ResidentRegisterController), welche den Prozess
anstösst, damit indirekt das Java Delegate und von da aus den Datenlayer
    4. XML-Schema Definition als Basis für das WSDL (eCH-0194-1-0.xsd)
11. Test-Fälle als soapUI-Projekt (ResidentRegisterServiceTests-soapui-project.xml)
12. initialData.sql mit INSERT-Statements für die Datenbank (Resident und ResidentRelation-Tabellen)

## Vorbereitungen, Deployment und Start
1. Wenn man die **Enterprise Edition** von Camunda verwenden will, benötigt man die Zugangsdaten zum Nexus Repository und eine gültige Lizenz. Wie man diese "installiert", steht in den Kommentaren im pom.xml.
2. **Erstmalig** oder bei Problemen ein **Clean & Build (Netbeans)**, respektive `mvn clean install` (Cmd) durchführen. Dabei werden im Target nebst den kompilierten Java-Klassen auch die aus dem eCH-0194-1-0.xsd abgeleiteten Java-Klassen generiert vom JAXB2-Maven-Plugin.
3. Bei Änderungen am POM-File oder bei **(Neu)kompilierungsbedarf** genügt ein **Build (Netbeans)**, respektive `mvn install`
4. Für den **Start** ist ein **Run (Netbeans)**, respektive `java -jar .\target\NAME DES JAR-FILES.jar` (Cmd) erforderlich. Dabei wird Tomcat gestartet, die Datenbank erstellt/hochgefahren, Camunda in der Version 7.9 mit den Prozessen und den Eigenschaften (application.properties) hochgefahren.
5. Die **Datenbank** personenregister.mv.db ist ebenfalls im Github-Projekt enthalten, so dass auch tatsächlich Einwohner gefunden werden können. Falls man in Schritt 3 die Datenbank gelöscht hat, kann man sich an der H2-Console anmelden (Details siehe unten) und die **INSERT-Statements** aus initialData.sql ausführen.
6. Das **Beenden** geschieht mit **Stop Build/Run (Netbeans)**, respektive **CTRL+C** (Cmd)
7. Falls man die bestehenden **Prozessdaten nicht mehr benötigt** und die Datenbank inzwischen recht angewachsen ist, genügt es, die Datei DATENBANKNAME.mv.db im Wurzelverzeichnis des Projekts zu löschen.

## Informationen für das Testen
### Tests mit soapUI
1. Starten wie im vorherigen Kapitel beschrieben, damit der SOAP WebService läuft und über die URL http://localhost:8090/soap
verfügbar ist
2. Testen über folgende Varianten:
    1. In soapUI händisch neue SOAP-Requests generieren mit der WSDL
http://localhost:8083/soap/PersonenRegisterService?wsdl. Welche Personen dabei
gefunden werden können, kann über die H2-Konsole geprüft werden in den Tabellen
RESIDENT und RESIDENT_RELATION. Hierzu auf http://localhost:8083/console anmelden 
mit Driver Class = org.h2.Driver, JDBC URL = jdbc:h2:./personenregister, User Name 
= sa, Password = Leer lassen
    2. Die vorgefertigten Requests/TestSuite nutzen im soapUI-Projekt \src\test\resources\
ResidentRegisterServiceTests-soapui-project.xml

### Tests aus der Umzugsplattform heraus
Hierzu den Anweisungen folgen in https://github.com/zhaw-gpi/eumzug_musterloesung

## Zugriff auf Camunda Webapps
Wohl selten erforderlich, aber falls doch: Anmelden mit Benutzername und Passwort a bei http://localhost:8083

## Offene Punkte
1. ResidentRegisterController:135:localPersonId sollte frisch pro Person generiert
werden und nicht von Hauptperson übernommen werden

## Prototypische Vereinfachungen
1. In personMoveRequest werden die municipalityId und municipalityName 
vermutlich deshalb mitgeliefert, weil eine Person in mehreren Gemeinden registriert
sein kann (Hauptwohnsitz und Wochenaufenthalter). Das heisst, moveAllowed müsste
eigentlich bei der Beziehungstabelle sein zwischen Municipality und Resident. Dies
mit JPA umzusetzen, ist gar nicht so einfach, u.a. braucht es dann eine eigene
Entität für diese Beziehungstabelle mit einem Pseudo-Primärschlüssel:
https://www.thoughts-on-java.org/many-relationships-additional-properties/. Daher
lassen wir dies weg.
2. Schema Validation: Mit @SchemaValidation bei der Webservice-Endpoint-Klasse
werden lediglich Standard-Fehlermeldungen ausgegeben, nicht aber benutzerdefinierte.
Das ist ok bei einem Prototyp. Störender ist, dass XJC keine Annotations für die
XSD-Restrictions hinzufügt (also, dass z.B. ein String nicht leer oder länger als
40 Zeichen sein darf). Es gibt teilweise Plugins, aber die sind seit mehreren
Jahren nicht mehr aktualisiert => man müsste entweder ein eigenes Plugin schreiben
oder aber die Annotations in den generierten Java-Klassen von Hand hinzufügen, aber
dann wäre die Generierung im Build-Prozess natürlich nicht mehr opportun.

## Mitwirkende
1. Björn Scheppler: Hauptarbeit
2. Peter Heinrich: Der stille Support im Hintergrund mit vielen Tipps sowie zuständig
für den Haupt-Stack mit SpringBoot & Co.
3. Gruppe TZb02 (Bekim Kadrija, Jovica Rajic, Luca Belmonte, Simon Bärtschi, Sven 
Baumann): Mitzuziehende Personen auswählen