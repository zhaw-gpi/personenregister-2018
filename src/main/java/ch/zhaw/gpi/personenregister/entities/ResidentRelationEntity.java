package ch.zhaw.gpi.personenregister.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Enitity-Klasse für Stammdaten zu Einwohnern im gleichen Haushalt
 * 
 * @author scep
 */
@Entity(name="ResidentRelation")
public class ResidentRelationEntity implements Serializable {
    // Manuell gesetzte Id (Familienname)
    @Id
    private String id;

    // Referenz auf Einwohner-Entitäten
    @OneToMany(mappedBy = "residentRelationEntity")
    private List<ResidentEntity> residentEntities;

    // GETTER und SETTER
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResidentEntity> getResidentEntities() {
        if(this.residentEntities == null){
            return new ArrayList<>();
        } else {
            return this.residentEntities;
        }
    }

    public void setResidentEntities(List<ResidentEntity> residentEntities) {
        this.residentEntities = residentEntities;
    }

    public void addResidentEntity(ResidentEntity residentEntity) {
        this.residentEntities.add(residentEntity);
        // In der Einwohner-Entität eine Verknüpfung mit dieser RelationEntity hinzufügen
        residentEntity.setResidentRelationEntity(this);
    }

    public void removeResidentEntity(ResidentEntity residentEntity) {
        this.residentEntities.remove(residentEntity);
        // In der Einwohner-Entität die Verknüpfung zur RelationEntity entfernen
        residentEntity.setResidentRelationEntity(null);
    }
}