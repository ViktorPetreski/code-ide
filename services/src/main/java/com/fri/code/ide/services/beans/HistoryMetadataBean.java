package com.fri.code.ide.services.beans;

import com.fri.code.ide.lib.HistoryMetadata;
import com.fri.code.ide.lib.IDEMetadata;
import com.fri.code.ide.models.converters.HistoryMetadataConverter;
import com.fri.code.ide.models.entities.HistoryMetadataEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class HistoryMetadataBean {
    private Logger log = Logger.getLogger(IDEMetadataBean.class.getName());
    @Inject
    private EntityManager em;

    private Client httpClient;

    @PostConstruct
    void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<HistoryMetadata> getHistoryForExercise(Integer exerciseID) {
        TypedQuery<HistoryMetadataEntity> query = em.createNamedQuery("HistoryMetadataEntity.getHistoryForExercise", HistoryMetadataEntity.class).setParameter(1, exerciseID);
        return query.getResultList().stream().map(HistoryMetadataConverter::toDTO).collect(Collectors.toList());
    }

    public List<HistoryMetadata> getAllCodeHistory() {
        TypedQuery<HistoryMetadataEntity> query = em.createNamedQuery("HistoryMetadataEntity.getAll", HistoryMetadataEntity.class);
        return query.getResultList().stream().map(HistoryMetadataConverter::toDTO).collect(Collectors.toList());
    }

    public void createHistoryMetadata(IDEMetadata ideMetadata) {
        HistoryMetadata metadata = new HistoryMetadata();
        metadata.setSubmitTime(LocalDateTime.now());
        metadata.setCode(ideMetadata.getCode());
        metadata.setExerciseID(ideMetadata.getExerciseID());
        HistoryMetadataEntity entity = HistoryMetadataConverter.toEntity(metadata);
        List<HistoryMetadata> historyMetadata = getHistoryForExercise(metadata.getExerciseID());
        log.warning(String.format("SIZE: %d", historyMetadata.size()));
        if (historyMetadata.size() >= 10) {
            HistoryMetadata oldestSubmit = historyMetadata.get(0);
            updateHistoryMetadata(oldestSubmit.getID(), metadata);
        }
        else {
            try {
                beginTx();
                em.persist(entity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }

            if (entity.getID() == null) {
                log.severe("NOT SAVED");
                throw new RuntimeException("The code was not saved");
            }
        }
    }

    public void updateHistoryMetadata(Integer historyID, HistoryMetadata updatedMetadata) {
        HistoryMetadataEntity entity = em.find(HistoryMetadataEntity.class, historyID);

        if (entity == null) {
            throw new RuntimeException("Script not found");
        }

        HistoryMetadataEntity updatedEntity = HistoryMetadataConverter.toEntity(updatedMetadata);
        try {
            beginTx();
            entity.setCode(updatedEntity.getCode());
            entity.setSubmitTime(updatedEntity.getSubmitTime());
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

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


}
