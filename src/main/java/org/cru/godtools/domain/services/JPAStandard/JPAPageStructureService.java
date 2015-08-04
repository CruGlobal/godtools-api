package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 7/14/15.
 */
@JPAStandard
public class JPAPageStructureService implements PageStructureService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public PageStructure selectById(UUID id) { return entityManager.find(PageStructure.class, id); }

    public List<PageStructure> selectByTranslationId(UUID translationId)
    {
        return entityManager.createQuery("FROM PageStructure WHERE translation.id = :translationId")
                .setParameter("translationId", translationId)
                .getResultList();
    }

    public PageStructure selectByTranslationIdAndFilename(UUID translationId, String filename)
    {
        return (PageStructure) entityManager.createQuery("FROM PageStructure WHERE translation.id = :translationId AND filename = :filename")
                .setParameter("translationId",translationId)
                .setParameter("filename",filename)
                .getSingleResult();
    }

    public void insert(PageStructure pageStructure) { entityManager.persist(pageStructure); }

    public void update(PageStructure pageStructure) { entityManager.merge(pageStructure); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear()
    {
        List<PageStructure> pageStructures = entityManager.createQuery("FROM PageStructure").getResultList();

        for(PageStructure pageStructure : pageStructures)
        {
            List<TranslationElement> translationElements = entityManager.createQuery("FROM TranslationElement WHERE pageStructure.id = :pageStructureId")
                    .setParameter("pageStructureId",pageStructure.getId())
                    .getResultList();

            for(TranslationElement translationElement : translationElements)
                (entityManager.find(TranslationElement.class, translationElement)).setPageStructure(null);

            entityManager.remove(entityManager.find(PageStructure.class, pageStructure.getId()));
        }
    }
}
