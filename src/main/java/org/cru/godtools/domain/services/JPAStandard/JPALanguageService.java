package org.cru.godtools.domain.services.JPAStandard;

import com.google.common.base.*;
import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.inject.*;
import java.util.*;
import java.util.List;

/**
 * Created by justinsturm on 7/10/15.
 */
@JPAStandard
public class JPALanguageService implements LanguageService
{
    private static final SessionFactory sessionFactory = buildSessionFactory();

    Logger log = Logger.getLogger(JPAImageService.class);

    private boolean autoCommit = true;

    @Inject @JPAStandard
    private PackageService packageService;

    private static final SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
            standardServiceRegistryBuilder.applySettings(configuration.getProperties());
            return configuration.buildSessionFactory(standardServiceRegistryBuilder.build());
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Language getOrCreateLanguage(LanguageCode languageCode)
    {
        log.info("Selecting or Inserting Language " + languageCode.getLanguageCode());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            Language language = selectByLanguageCode(languageCode);
            if(language==null)
            {
                txn.begin();
                language = new Language();
                language.setId(UUID.randomUUID());
                language.setFromLanguageCode(languageCode);
                session.save(language);
                txn.commit();
            }

            return language;
        }
        catch(Exception e)
        {
            if(txn!=null)
            {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        }
        finally
        {
            if(session!=null)
            {
                session.close();
            }
        }
    }

    public List<Language> selectAllLanguages()
    {
        log.info("Getting All Languages");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            List languages = session.createQuery("FROM Language").list();
            txn.commit();

            return languages;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Language selectLanguageById(UUID id)
    {
        log.info("Getting Language with Id " + id);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            Language language = (Language) session.get(Language.class,id);
            txn.commit();

            return language;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Language selectByLanguageCode(LanguageCode languageCode)
    {
        log.info("Getting Language with code " + languageCode.getLanguageCode());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            List<Language> languages = session.createQuery("FROM Language WHERE code = :code")
                    .setString("code",languageCode.getLanguageCode())
                    .list();
            txn.commit();

            for(Language language : languages)
            {
                boolean matched = true;

                if(!Strings.nullToEmpty(languageCode.getLocaleCode()).equals(Strings.nullToEmpty(language.getLocale()))) matched = false;
                if(!Strings.nullToEmpty(languageCode.getSubculture()).equals(Strings.nullToEmpty(language.getSubculture()))) matched = false;

                if(matched) return language;
            }
            return null;
        }
        catch (Exception e)
        {
            if (txn != null)
            {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        }
        finally
        {
            if (session != null)
            {
                session.close();
            }
        }
    }

    public boolean languageExists(Language language)
    {
        log.info("Exists? " + language.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            Language language1 = (Language) session.get(Language.class, language.getId());
            txn.commit();
            if(language1!=null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch(Exception e)
        {
            if(txn!=null)
            {
                txn.rollback();
            }

            e.printStackTrace();
            return false;
        }
        finally
        {
            if(session!=null)
            {
                session.close();
            }
        }
    }

    public void insert(Language language)
    {
        log.info("Inserting Language with Id " + language.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            session.save(language);
            txn.commit();
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }

            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void setAutoCommit(boolean autoCommit)
    {
        this.autoCommit = autoCommit;
    }

    public void rollback()
    {
        log.info("Deleting for JPA Testing");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        if(!autoCommit) {
            try {
                txn.begin();

                for(Language language : selectAllLanguages())
                {
                    for(Package packageWithDefaultLanguage : language.getPackages())
                    {
                        packageWithDefaultLanguage.setDefaultLanguage(null);
                    }

                    session.delete(language);
                }

                txn.commit();
            } catch (Exception e) {
                if (txn != null) {
                    txn.rollback();
                }

                e.printStackTrace();
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        }
    }
}
