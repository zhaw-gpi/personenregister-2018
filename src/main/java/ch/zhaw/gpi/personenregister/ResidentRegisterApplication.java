package ch.zhaw.gpi.personenregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse mit Main-Methode, um die SpringBoot-Applikation zu starten
 * 
 * @author scep
 */
@SpringBootApplication
public class ResidentRegisterApplication {

    /**
     * Main-Methode
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Startet eine Spring-Boot-Applikation basierend auf dieser Klasse
        SpringApplication.run(ResidentRegisterApplication.class, args);
    }
}
