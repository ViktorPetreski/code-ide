package com.fri.code.ide.lib;

import java.util.List;
import java.util.TreeSet;

public class IDEMetadata {
    private Integer ID;
    private String code;
    private Integer exerciseID;
    private List<HistoryMetadata> codeHistory;

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

    public List<HistoryMetadata> getCodeHistory() {
        return codeHistory;
    }

    public void setCodeHistory(List<HistoryMetadata> codeHistory) {
        this.codeHistory = codeHistory;
    }
}


