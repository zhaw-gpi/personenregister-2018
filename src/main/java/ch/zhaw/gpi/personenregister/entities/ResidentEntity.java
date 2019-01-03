package ch.zhaw.gpi.personenregister.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Enitity-Klasse für Stammdaten zu einem Einwohner
 * 
 * @author scep
 */
@Entity(name = "Resident")
public class ResidentEntity implements Serializable {

    // Automatisch generierte PersonenId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    // Vorname
    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    // Nachname
    @NotNull
    @Size(min = 1, max = 100)
    private String officialName;

    // Geschlecht (1 = männlich, 2 = weiblich, 3 = unbestimmt)
    @NotNull
    @Min(value = 1)
    @Max(value = 3)
    private int sex;

    // Geburtsdatum
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    // Elektronische Umzugsmeldung erlaubt?
    @NotNull
    private Boolean moveAllowed;

    // Referenz auf eine Haushalt-Entität, in welcher mehr als ein Einwohner leben können
    // Dank CascadeType.ALL wird ein Haushalt gelöscht, wenn keine Einwohner mehr darin sind
    // und umgekehrt wird ein Haushalt mitgespeichert, wenn ein Einwohner persistiert wird
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "HOUSEHOLD_ID")
    private HouseHoldEntity houseHoldEntity;

    // GETTER und SETTER
    public Long getPersonId() {
        return this.personId;
    }

    public ResidentEntity setPersonId(Long personId) {
        this.personId = personId;
        return this;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public ResidentEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getOfficialName() {
        return this.officialName;
    }

    public ResidentEntity setOfficialName(String officialName) {
        this.officialName = officialName;
        return this;
    }

    public int getSex() {
        return this.sex;
    }

    public ResidentEntity setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public ResidentEntity setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public boolean isMoveAllowed() {
        return this.moveAllowed;
    }

    public ResidentEntity setMoveAllowed(boolean moveAllowed) {
        this.moveAllowed = moveAllowed;
        return this;
    }

    public HouseHoldEntity getHouseHoldEntity() {
        return this.houseHoldEntity;
    }

    public ResidentEntity setHouseHoldEntity(HouseHoldEntity houseHoldEntity) {
        this.houseHoldEntity = houseHoldEntity;
        return this;
    }
}