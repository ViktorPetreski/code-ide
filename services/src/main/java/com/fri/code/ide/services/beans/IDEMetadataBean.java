package com.fri.code.ide.services.beans;

import com.fri.code.ide.lib.HistoryMetadata;
import com.fri.code.ide.lib.IDEMetadata;
import com.fri.code.ide.models.converters.IDEMetadataConverter;
import com.fri.code.ide.models.entities.HistoryMetadataEntity;
import com.fri.code.ide.models.entities.IDEMetadataEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class IDEMetadataBean {
    private Logger log = Logger.getLogger(IDEMetadataBean.class.getName());
    @Inject
    private EntityManager em;

    private Client httpClient;

    @PostConstruct
    void init() {
        httpClient = ClientBuilder.newClient();
    }


    public IDEMetadata getIDEForExercise(Integer exerciseID) {
        TypedQuery<IDEMetadataEntity> query = em.createNamedQuery("IDEMetadataEntity.getIDEForExercise", IDEMetadataEntity.class).setParameter(1, exerciseID);
        log.info(String.format("Get ide for %d exercise", exerciseID));
        return IDEMetadataConverter.toDTO(query.getSingleResult());
    }

    public IDEMetadata createIDEMetadata(IDEMetadata metadata) {
        IDEMetadataEntity entity = IDEMetadataConverter.toEntity(metadata);

        try {
            beginTx();
            em.persist(entity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (entity.getID() == null) {
            throw new RuntimeException("The code was not saved");
        }

        return IDEMetadataConverter.toDTO(entity);
    }

    public IDEMetadata updateIDEMetadata(Integer scriptID, IDEMetadata updatedMetadata) {
        IDEMetadataEntity entity = em.find(IDEMetadataEntity.class, scriptID);

        if (entity == null) {
            log.severe("NOT FOUND");
            throw new RuntimeException("Script not found");
        }

        IDEMetadataEntity updatedEntity = IDEMetadataConverter.toEntity(updatedMetadata);
        try {
            beginTx();
            entity.setCode(updatedEntity.getCode());
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
        return IDEMetadataConverter.toDTO(entity);
    }

    public IDEMetadata revertToCodeHistory(Integer historyID) {
        HistoryMetadataEntity historyMetadataEntity = em.find(HistoryMetadataEntity.class, historyID);

        if (historyMetadataEntity == null) {
            throw new RuntimeException("Code history not found");
        }

        IDEMetadata ideMetadata = getIDEForExercise(historyMetadataEntity.getExerciseID());
        ideMetadata.setCode(historyMetadataEntity.getCode());

        return updateIDEMetadata(ideMetadata.getID(), ideMetadata);
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public List<IDEMetadata> getAllScripts() {
        TypedQuery<IDEMetadataEntity> query = em.createNamedQuery("IDEMetadataEntity.getAll", IDEMetadataEntity.class);
        return query.getResultList().stream().map(IDEMetadataConverter::toDTO).collect(Collectors.toList());
    }
}
