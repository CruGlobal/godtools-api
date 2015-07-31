package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.cru.godtools.domain.translations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/14/15.
 */
@JPAStandard
public class JPAPageStructureService implements PageStructureService
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

    public PageStructure selectById(UUID id)
    {
        log.info("Select Page Structure with Id " + id);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            PageStructure pageStructure = (PageStructure) session.get(PageStructure.class, id);
            txn.commit();

            return pageStructure;
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

    public List<PageStructure> selectByTranslationId(UUID translationId)
    {
        log.info("Selecting Page Structure with Translation Id " + translationId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List pageStructures = session.createQuery("FROM PageStructure WHERE translation.id = :translationId")
                    .setParameter("translationId", translationId)
                    .list();
            txn.commit();

            return pageStructures;
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

    public PageStructure selectByTranslationIdAndFilename(UUID translationId, String filename)
    {
        log.info("Selecting Page Structure with Translation Id " + translationId + " and File Name " + filename);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            PageStructure pageStructure = (PageStructure) session.createQuery("FROM PageStructure WHERE translation.id = :translationId AND filename = :filename")
                    .setParameter("translationId",translationId)
                    .setString("filename",filename)
                    .uniqueResult();
            txn.commit();

            return pageStructure;
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
        finally {
            if(session!=null)
            {
                session.close();
            }
        }
    }

    public void insert(PageStructure pageStructure)
    {
        log.info("Inserting Page Structure with Id " + pageStructure.getId());

        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(pageStructure);
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
        finally {
            if(session!=null)
            {
                session.close();
            }
        }
    }

    public void update(PageStructure pageStructure)
    {
        log.info("Updating Page Structure with Id" + pageStructure.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.update(pageStructure);
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
        finally {
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

                PageStructure persistentPageStructure;
                List<PageStructure> pageStructures = session.createQuery("FROM PageStructure").list();

                for(PageStructure pageStructure : pageStructures)
                {
                    List<TranslationElement> translationElements = session.createQuery("FROM TranslationElement WHERE pageStructure.id = :pageStructureId")
                            .setParameter("pageStructureId",pageStructure.getId())
                            .list();

                    for(TranslationElement translationElement : translationElements)
                    {
                        translationElement.setPageStructure(null);
                        session.update(translationElement);
                    }

                    persistentPageStructure = (PageStructure) session.load(PageStructure.class,pageStructure.getId());
                    session.delete(persistentPageStructure);
                }

                txn.commit();
            }
            catch (Exception e)
            {
                if (txn!=null)
                {
                    txn.rollback();
                }

                e.printStackTrace();
            }
            finally {
                if(session!=null)
                {
                    session.close();
                }
            }
        }
    }
}
