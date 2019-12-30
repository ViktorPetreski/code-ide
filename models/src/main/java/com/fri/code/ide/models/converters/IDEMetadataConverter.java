package com.fri.code.ide.models.converters;


import com.fri.code.ide.lib.IDEMetadata;
import com.fri.code.ide.models.entities.IDEMetadataEntity;

public class IDEMetadataConverter {
    public static IDEMetadata toDTO(IDEMetadataEntity entity) {
        IDEMetadata metadata = new IDEMetadata();
        metadata.setCode(entity.getCode());
        metadata.setExerciseID(entity.getExerciseID());
        metadata.setID(entity.getID());
        return metadata;
    }

    public static IDEMetadataEntity toEntity(IDEMetadata metadata) {
        IDEMetadataEntity entity = new IDEMetadataEntity();
        entity.setCode(metadata.getCode());
        entity.setExerciseID(metadata.getExerciseID());
        entity.setID(metadata.getID());
        return entity;
    }

}
