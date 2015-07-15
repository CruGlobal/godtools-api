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
                    .setEntity("languageId",languageId)
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
                    .setEntity("languageId",languageId)
                    .setEntity("released",released)
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
                    .setEntity("packageId",packageId)
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
                    .setEntity("languageId",languageId)
                    .setEntity("packageId",packageId)
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

        try
        {
            txn.begin();
            Translation translation = (Translation) session.createQuery("FROM Translation WHERE languageId = :languageId AND packageId = :packageId AND versionNumber = :versionNumber")
                    .setEntity("languageId",languageId)
                    .setEntity("packageId",packageId)
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

    public void insert(Translation translation)
    {
        log.info("Insert Translation with Id " + translation.getId());
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
