package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.*;
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
 * Created by justinsturm on 7/15/15.
 */
@JPAStandard
public class JPATranslationService implements TranslationService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public Translation selectById(UUID id) { return entityManager.find(Translation.class, id); }

    public List<Translation> selectAll() {
        return entityManager.createQuery("FROM Translation").getResultList();
    }

    public List<Translation> selectByLanguageId(UUID languageId) {
        return entityManager.createQuery("FROM Translation WHERE language.id = :languageId")
                .setParameter("languageId", languageId)
                .getResultList();
    }

    public List<Translation> selectByLanguageIdReleased(UUID languageId, boolean released) {
        return entityManager.createQuery("FROM Translation WHERE language.id = :languageId AND released = :released")
                    .setParameter("languageId", languageId)
                    .setParameter("released", released)
                    .getResultList();
    }

    public List<Translation> selectByPackageId(UUID packageId) {
        return entityManager.createQuery("FROM Translation WHERE gtPackage.id = :packageId")
                    .setParameter("packageId",packageId)
                    .getResultList();
    }

    public List<Translation> selectByLanguageIdPackageId(UUID languageId, UUID packageId) {
        return entityManager.createQuery("FROM Translation WHERE language.id = :languageId AND gtPackage.id = :packageId")
                    .setParameter("languageId", languageId)
                    .setParameter("packageId", packageId)
                    .getResultList();
    }

    public Translation selectByLanguageIdPackageIdVersionNumber(UUID languageId, UUID packageId, GodToolsVersion godToolsVersion)
    {
        if(godToolsVersion == GodToolsVersion.LATEST_VERSION)
            return returnLatestVersion(languageId, packageId);
        else if(godToolsVersion == GodToolsVersion.LATEST_PUBLISHED_VERSION)
            return returnLatestPublishedVersion(languageId, packageId);
        else if(godToolsVersion == GodToolsVersion.DRAFT_VERSION)
            return returnDraftVersion(languageId, packageId);
        else
            return (Translation) entityManager.createQuery("FROM Translation WHERE language.id = :languageId AND gtPackage.id = :packageId AND versionNumber = :versionNumber")
                    .setParameter("languageId",languageId)
                    .setParameter("packageId",packageId)
                    .setParameter("versionNumber",godToolsVersion.getTranslationVersion())
                    .getSingleResult();
    }

    private Translation returnDraftVersion(UUID languageId, UUID packageId)
    {
        for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
            if(!translation.isReleased())
                return translation;
        return null;
    }

    private Translation returnLatestPublishedVersion(UUID languageId, UUID packageId)
    {
        Translation highestFoundVersionTranslation = null;
        for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
            if(translation.isReleased() && (highestFoundVersionTranslation == null || translation.getVersionNumber().compareTo(highestFoundVersionTranslation.getVersionNumber()) > 0))
                highestFoundVersionTranslation = translation;
        return highestFoundVersionTranslation;
    }

    private Translation returnLatestVersion(UUID languageId, UUID packageId)
    {
        Translation highestFoundVersionTranslation = null;
        for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
            if(highestFoundVersionTranslation == null || translation.getVersionNumber().compareTo(highestFoundVersionTranslation.getVersionNumber()) > 0)
                highestFoundVersionTranslation = translation;
        return highestFoundVersionTranslation;
    }

    public void insert(Translation translation) { entityManager.persist(translation); }

    public void update(Translation translation) { entityManager.merge(translation); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear()
    {
        List<Translation> translations = selectAll();

        for(Translation translation : translations) {
            List<PageStructure> pageStructures = entityManager.createQuery("FROM PageStructure WHERE translation.id = :translationId")
                    .setParameter("translationId", translation.getId())
                    .getResultList();

            for (PageStructure pageStructure : pageStructures)
                entityManager.find(PageStructure.class, pageStructure.getId()).setTranslation(null);

            List<TranslationElement> translationElements = entityManager.createQuery("FROM TranslationElement WHERE id.translation.id = :translationId")
                    .setParameter("translationId", translation.getId())
                    .getResultList();

            for (TranslationElement translationElement : translationElements)
                entityManager.find(TranslationElement.class, translationElement.getId()).setTranslation(null);

            entityManager.remove(entityManager.find(Translation.class, translation.getId()));
        }
    }
}
