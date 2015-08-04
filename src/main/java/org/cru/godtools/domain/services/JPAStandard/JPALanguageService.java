package org.cru.godtools.domain.services.JPAStandard;

import com.google.common.base.*;
import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.model.Package;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;
import java.util.List;

/**
 * Created by justinsturm on 7/10/15.
 */
@JPAStandard
public class JPALanguageService implements LanguageService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public Language getOrCreateLanguage(LanguageCode languageCode)
    {
        Language language = selectByLanguageCode(languageCode);

        if(language==null)
        {
            language = new Language();
            language.setId(UUID.randomUUID());
            language.setFromLanguageCode(languageCode);
            entityManager.persist(language);
        }

        return language;
    }

    public List<Language> selectAllLanguages() { return entityManager.createQuery("FROM Language").getResultList(); }

    public Language selectLanguageById(UUID id) { return entityManager.find(Language.class, id); }

    public Language selectByLanguageCode(LanguageCode languageCode)
    {
        List<Language> languages = entityManager.createQuery("FROM Language WHERE code = :code")
                .setParameter("code", languageCode.getLanguageCode())
                .getResultList();

        for(Language language : languages)
        {
            if(Strings.nullToEmpty(languageCode.getLocaleCode()).equals(Strings.nullToEmpty(language.getLocale()))
                    &&(Strings.nullToEmpty(languageCode.getSubculture()).equals(Strings.nullToEmpty(language.getSubculture()))))
                return language;
        }

        return null;
    }

    public boolean languageExists(Language language)
    {
        Language foundLanguage = entityManager.find(Language.class, language.getId());

        if(foundLanguage!=null)
            return true;
        else
            return false;
    }

    public void insert(Language language) { entityManager.persist(language); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear()
    {
        List<Language> languages = selectAllLanguages();

        for(Language language : languages)
        {
            List<Package> packages = entityManager.createQuery("FROM Package WHERE defaultLanguage.id = :languageId")
                    .setParameter("languageId", language.getId())
                    .getResultList();

            for(Package gtPackage : packages) {
                (entityManager.find(Package.class,gtPackage.getId())).setDefaultLanguage(null);}

            List<Translation> translations = entityManager.createQuery("FROM Translation WHERE language.id = :languageId")
                    .setParameter("languageId", language.getId())
                    .getResultList();

            for(Translation translation : translations) {
                (entityManager.find(Translation.class,translation.getId())).setLanguage(null);}

            entityManager.remove(entityManager.find(Language.class, language.getId()));
        }
    }
}
