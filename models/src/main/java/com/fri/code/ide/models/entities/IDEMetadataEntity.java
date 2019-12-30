package com.fri.code.ide.models.entities;


import javax.persistence.*;

@Entity
@Table(name="ide_entity")
@NamedQueries(
        value = {
                @NamedQuery(name = "IDEMetadataEntity.getAll", query = "SELECT inp FROM IDEMetadataEntity inp"),
                @NamedQuery(name = "IDEMetadataEntity.getIDEForExercise", query = "SELECT inp FROM IDEMetadataEntity inp WHERE inp.exerciseID = ?1")
        }
)
public class IDEMetadataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name = "code")
    private String code;

    @Column(name = "exerciseID")
    private Integer exerciseID;


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(Integer exerciseID) {
        this.exerciseID = exerciseID;
    }

}
