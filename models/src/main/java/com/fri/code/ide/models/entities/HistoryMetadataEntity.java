package com.fri.code.ide.models.entities;



import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_entity")
@NamedQueries(
        value = {
                @NamedQuery(name = "HistoryMetadataEntity.getAll", query = "SELECT inp FROM HistoryMetadataEntity inp"),
                @NamedQuery(name = "HistoryMetadataEntity.getHistoryForExercise", query = "SELECT inp FROM HistoryMetadataEntity inp WHERE inp.exerciseID = ?1 ORDER BY inp.submitTime ASC")
        }
)
public class HistoryMetadataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name="code")
    private String code;

    @Column(name="submitTime")
    private LocalDateTime submitTime;

    @Column(name = "exerciseID")
    private Integer exerciseID;

    public Integer getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(Integer exerciseID) {
        this.exerciseID = exerciseID;
    }

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

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

}

