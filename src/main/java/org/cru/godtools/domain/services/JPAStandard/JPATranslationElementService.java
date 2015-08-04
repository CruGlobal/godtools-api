package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.Query;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 7/15/15.
 */
@JPAStandard
public class JPATranslationElementService implements TranslationElementService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public List<TranslationElement> selectByTranslationId(UUID translationId, String ... orderByFields) {
        StringBuilder orderByBuilder = null;

        if(orderByFields != null && orderByFields.length > 0)
        {
            orderByBuilder = new StringBuilder(" ORDER BY ");
            boolean isFirst = true;
            for(String orderByField : orderByFields)
            {
                if(!isFirst) orderByBuilder.append(", ");
                orderByBuilder.append(orderByField);
                isFirst = false;
            }
        }

        return entityManager.createQuery("FROM TranslationElement WHERE translationId = :translationId"
                + orderByBuilder != null ? orderByBuilder.toString() : "")
                .setParameter("translationId",translationId)
                .getResultList();
    }

    public List<TranslationElement> selectByTranslationIdPageStructureId(UUID translationId, UUID pageStructureId) {
        return entityManager.createQuery("FROM TranslationElement WHERE translationElementId.translation.id = :translationId AND pageStructure.id = :pageStructureId")
                .setParameter("translationId",translationId)
                .setParameter("pageStructureId",pageStructureId)
                .getResultList();
    }

    public TranslationElement selectyByIdTranslationId(UUID id, UUID translationId) {
        return (TranslationElement) entityManager.createQuery("FROM TranslationElement WHERE id = :id AND translationElementId.translation.id = :translationId")
                .setParameter("id", id)
                .setParameter("translationId", translationId)
                .getSingleResult();
    }

    public void insert(TranslationElement translationElement) { entityManager.persist(translationElement); }

    public void update(TranslationElement translationElement) { entityManager.merge(translationElement); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear() { entityManager.createQuery("DELETE FROM TranslationElement"); }
}
