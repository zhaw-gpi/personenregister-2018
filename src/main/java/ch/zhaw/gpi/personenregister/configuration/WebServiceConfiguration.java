package ch.zhaw.gpi.personenregister.configuration;

import ch.zhaw.gpi.personenregister.endpoint.PersonenRegisterServiceEndpoint;
import javax.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Konfiguriert die Web Service-Komponenten dieser Applikation:
 * 1. Servlet, welches die HTTP-Anfragen entgegen nimmt und beantwortet
 * 2. SpringBus, welcher alle Komponenten des Apache CXF-Frameworks lädt
 * 3. Der Endpunkt, also die Schnittstelle, welche unser Web Service
 *    zur Verfügung stellt und die "Verlinkung" zur eigentlichen Implementation
 *    hinter dieser Schnittstelle
 * 
 * @Configuration-Annotation stellt sicher, dass diese Klasse von 
 * CamundaSpringBootStarter als zu berücksichtigende Konfigurations-Klasse gefunden wird
 */
@Configuration
public class WebServiceConfiguration {    

    /**
     * Registriert ein Apache CXF-Servlet im Tomcat-Servlet-Container mit Angabe
     * der URI (http://localhost:PORT/soap/) unter welche unsere Web Services
     * (wir haben nur einen) erreicht werden können. Dieses CXF-Servlet nimmt die
     * Anfragen entgegen, versucht einen zum Aufruf passenden Controller zu finden,
     * übersetzt die SOAP (XML)-Nachricht in ein Java-Objekt, welches dem passenden
     * Controller übergeben wird. Dieser führt die Geschäftslogik aus und gibt 
     * die Antwort als Java-Objekt zurück an das CXF-Servlet, welches die Antwort
     * in die definierte SOAP-Nachrichtenstruktur übersetzt und zurück an den 
     * Client gibt.
     * @return 
     */
    @Bean
    public ServletRegistrationBean dispatcherServletWebService() {
        return new ServletRegistrationBean(new CXFServlet(), "/soap/*");
    }

    /**
     * Lädt alle benötigten Komponenten von Apache CXF in ein SpringBus-Objekt,
     * welches vereinfacht ausgedrückt den Zugriff auf alle Funktionen im
     * Zusammenhang mit SOAP Webservices erlaubt. Über DEFAULT_BUS_ID geben
     * wir an, dass wir die 08:15-Konfiguration habn wollen
     * @return 
     */
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    /**
     * Die Endpunkt-Schnittstelle (siehe nächste Methode) benötigt die Angabe,
     * in welcher Klasse die weiteren Angaben zum Web Service zu finden sind, wie
     * z.B. die Operationen, welche zur Verfügung stehen. In dieser Klasse findet
     * dann auch der Aufruf der eigentlichen Implementierungsklassen statt, damit
     * Schnittstelle und Implementation sauber getrennt sind.
     * @return 
     */
    @Bean
    public PersonenRegisterServiceEndpoint personenRegisterService() {
        return new PersonenRegisterServiceEndpoint();
    }

    /**
     * Definition der Endpunkt-Schnittstelle unter Verwendung vom CXF-Servlet,
     * den CXF-Komponenten (springBus) sowie des Objekts, welche die Schnittstelle
     * weiter definiert (personenRegisterService).
     * 
     * @DependsOn referenziert das oben registrierten CXF-Servlet
     * @return 
     */
    @DependsOn("dispatcherServletWebService")
    @Bean
    public Endpoint jaxwsEndpoint() {
        // Deklaration einer Endpoint-Variable
        EndpointImpl endpoint;
        // Eine neue Endpoint-Implementation unter Verwendung der CXF-Komponenten
        // und dem Schnittstellen-Definitions-Objekt wird der endpoint-Variable zugewiesen
        endpoint = new EndpointImpl(springBus(), personenRegisterService());
        // Der Endpoint wird publiziert, das heisst, ab diesem Zeitpunkt kann über
        // http://localhost:PORT/soap/PersonenRegisterService auf den Webservice
        // zugegriffen werden
        endpoint.publish("/PersonenRegisterService");
        // Der Endpoint wird an die SpringBootApplication zurück gegeben
        return endpoint;
    }
}
