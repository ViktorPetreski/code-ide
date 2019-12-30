package com.fri.code.ide.models.converters;

import com.fri.code.ide.lib.HistoryMetadata;
import com.fri.code.ide.models.entities.HistoryMetadataEntity;

public class HistoryMetadataConverter {
    public static HistoryMetadata toDTO(HistoryMetadataEntity entity) {
        HistoryMetadata metadata = new HistoryMetadata();
        metadata.setCode(entity.getCode());
        metadata.setSubmitTime(entity.getSubmitTime());
        metadata.setID(entity.getID());
        metadata.setExerciseID(entity.getExerciseID());
        return metadata;
    }

    public static HistoryMetadataEntity toEntity(HistoryMetadata metadata) {
        HistoryMetadataEntity entity = new HistoryMetadataEntity();
        entity.setCode(metadata.getCode());
        entity.setSubmitTime(metadata.getSubmitTime());
        entity.setID(metadata.getID());
        entity.setExerciseID(metadata.getExerciseID());
        return entity;
    }
}
