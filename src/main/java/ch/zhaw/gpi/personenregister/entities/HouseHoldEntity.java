package ch.zhaw.gpi.personenregister.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Enitity-Klasse für Stammdaten zu Einwohnern im gleichen Haushalt
 * 
 * @author scep
 */
@Entity(name="Household")
public class HouseHoldEntity implements Serializable {
    // Automatisch gesetzte Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GETTER und SETTER
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}