package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/15/15.
 */
@JPAStandard
public class JPATranslationElementService implements TranslationElementService
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

    public List<TranslationElement> selectByTranslationId(UUID translationId, String ... orderByFields)
    {
        log.info("Select Translation Element with Translation Id " + translationId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

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

        try
        {
            txn.begin();
            List translationElements = session.createQuery("FROM TranslationElement WHERE translationId = :translationId"
                    + orderByBuilder != null ? orderByBuilder.toString() : "")
                    .setEntity("translationId",translationId)
                    .list();
            txn.commit();

            return translationElements;
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

    public List<TranslationElement> selectByTranslationIdPageStructureId(UUID translationId, UUID pageStructureId)
    {
        log.info("Select Translation Element with Translation Id " + translationId + " AND Structure Id " + pageStructureId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List translationElements = session.createQuery("FROM TranslationElement WHERE translationId = :translationId AND pageStructureId = :pageStructureId")
                    .setEntity("translationId",translationId)
                    .setEntity("pageStructureId",pageStructureId)
                    .list();
            txn.commit();
            txn.commit();

            return translationElements;
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

    public TranslationElement selectyByIdTranslationId(UUID id, UUID translationId)
    {
        log.info("Select Translation Element with Id " + id + "and Translation Id " + translationId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            TranslationElement translationElement = (TranslationElement) session.createQuery("FROM TranslationElement WHERE id = :id AND translationId = :translationId")
                    .setEntity("id", id)
                    .setEntity("translationId", translationId)
                    .uniqueResult();
            txn.commit();

            return translationElement;
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

    public void insert(TranslationElement translationElement)
    {
        log.info("Insert Translation Element with Id " + translationElement.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(translationElement);
            txn.commit();
        }
        catch(Exception e)
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

    public void update(TranslationElement translationElement)
    {
        log.info("Update Translation Element with Id " + translationElement.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.update(translationElement);
            txn.commit();
        }
        catch(Exception e)
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
                Query q1 = session.createQuery("DELETE FROM TranslationElement");
                q1.executeUpdate();
                txn.commit();
            }
            catch(Exception e)
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
