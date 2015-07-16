package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.cru.godtools.domain.translations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/15/15.
 */
@JPAStandard
public class JPATranslationService implements TranslationService
{
    private static final SessionFactory sessionFactory = buildSessionFactory();

    Logger log = Logger.getLogger(JPANotificationService.class);

    private boolean autoCommit = true;

    private static final SessionFactory buildSessionFactory()
    {
        try
        {
            Configuration configuration = new Configuration().configure();
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
            standardServiceRegistryBuilder.applySettings(configuration.getProperties());
            return configuration.buildSessionFactory( standardServiceRegistryBuilder.build());
        }
        catch( Throwable ex )
        {
            System.err.println("Initial SessionFactory creation failed");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Translation selectById(UUID id)
    {
        log.info("Select Translation with Id " + id);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            Translation translation = (Translation) session.get(Translation.class, id);
            txn.commit();

            return translation;
        }
        catch (Exception e)
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

    public List<Translation> selectByLanguageId(UUID languageId)
    {
        log.info("Select Translation with Language Id " + languageId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List translations = session.createQuery("FROM Translation WHERE languageId = :languageId")
                    .setParameter("languageId",languageId)
                    .list();
            txn.commit();

            return translations;
        }
        catch (Exception e)
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

    public List<Translation> selectByLanguageIdReleased(UUID languageId, boolean released)
    {
        log.info("Select Translation with Language Id " + languageId + " and released = " + released);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List translations = session.createQuery("FROM Translation WHERE languageId = :languageId AND released = :released")
                    .setParameter("languageId", languageId)
                    .setBoolean("released",released)
                    .list();
            txn.commit();

            return translations;
        }
        catch (Exception e)
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

    public List<Translation> selectByPackageId(UUID packageId)
    {
        log.info("Select Translation with Package Id " + packageId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List translations = session.createQuery("FROM Translation WHERE packageId = :packageId")
                    .setParameter("packageId",packageId)
                    .list();
            txn.commit();

            return translations;
        }
        catch (Exception e)
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

    public List<Translation> selectByLanguageIdPackageId(UUID languageId, UUID packageId)
    {
        log.info("Select Translation with Language Id " + languageId + " and Package Id " + packageId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            List translations = session.createQuery("FROM Translation WHERE languageId = :languageId AND packageId = :packageId")
                    .setParameter("languageId", languageId)
                    .setParameter("packageId", packageId)
                    .list();
            txn.commit();

            return translations;
        }
        catch (Exception e)
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

    public Translation selectByLanguageIdPackageIdVersionNumber(UUID languageId, UUID packageId, GodToolsVersion godToolsVersion)
    {
        log.info("Select Translation with Language Id " + languageId + " and Package Id " + packageId + " and Version Number " + godToolsVersion.getTranslationVersion());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        if(godToolsVersion == GodToolsVersion.LATEST_VERSION)
        {
            return returnLatestVersion(languageId, packageId);
        }
        else if(godToolsVersion == GodToolsVersion.LATEST_PUBLISHED_VERSION)
        {
            return returnLatestPublishedVersion(languageId, packageId);
        }
        else if(godToolsVersion == GodToolsVersion.DRAFT_VERSION)
        {
            return returnDraftVersion(languageId, packageId);
        }
        else
        {
            try
            {
                txn.begin();
                Translation translation = (Translation) session.createQuery("FROM Translation WHERE languageId = :languageId AND packageId = :packageId AND versionNumber = :versionNumber")
                        .setParameter("languageId",languageId)
                        .setParameter("packageId",packageId)
                        .setInteger("versionNumber",godToolsVersion.getTranslationVersion())
                        .uniqueResult();
                txn.commit();

                return translation;
            }
            catch (Exception e)
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
    }

    private Translation returnDraftVersion(UUID languageId, UUID packageId)
    {
        for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
        {
            if(!translation.isReleased())
            {
                return translation;
            }
        }
        return null;
    }

    private Translation returnLatestPublishedVersion(UUID languageId, UUID packageId)
    {
        Translation highestFoundVersionTranslation = null;

        for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
        {
            if(translation.isReleased() && (highestFoundVersionTranslation == null || translation.getVersionNumber().compareTo(highestFoundVersionTranslation.getVersionNumber()) > 0))
            {
                highestFoundVersionTranslation = translation;
            }
        }

        return highestFoundVersionTranslation;
    }

    private Translation returnLatestVersion(UUID languageId, UUID packageId)
    {
        Translation highestFoundVersionTranslation = null;

        for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
        {
            if(highestFoundVersionTranslation == null || translation.getVersionNumber().compareTo(highestFoundVersionTranslation.getVersionNumber()) > 0)
            {
                highestFoundVersionTranslation = translation;
            }
        }

        return highestFoundVersionTranslation;
    }

    public void insert(Translation translation)
    {
        log.info("Insert Translation with Id " + translation.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(translation);
            txn.commit();
        }
        catch (Exception e)
        {
            if(txn!=null)
            {
                txn.rollback();
            }

            e.printStackTrace();
        }
        finally
        {
            if(session!=null)
            {
                session.close();
            }
        }
    }

    public void update(Translation translation)
    {
        log.info("Update Translation with Id " + translation.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.update(translation);
            txn.commit();
        }
        catch (Exception e)
        {
            if(txn!=null)
            {
                txn.rollback();
            }

            e.printStackTrace();
        }
        finally
        {
            if(session!=null)
            {
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
        log.info("JPA Delete for Testing");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        if(!autoCommit)
        {
            try
            {
                txn.begin();
                Query q1 = session.createQuery("DELETE FROM Translation");
                q1.executeUpdate();
                txn.commit();
            }
            catch (Exception e)
            {
                if(txn!=null)
                {
                    txn.rollback();
                }

                e.printStackTrace();
            }
            finally
            {
                if(session!=null)
                {
                    session.close();
                }
            }
        }
    }
}
