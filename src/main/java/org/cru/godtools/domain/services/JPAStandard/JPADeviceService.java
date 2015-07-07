package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.notifications.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/7/15.
 */
@JPAStandard
public class JPADeviceService implements DeviceService
{

    private static final SessionFactory sessionFactory = buildSessionFactory();

    Logger log = Logger.getLogger(JPADeviceService.class);

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

    public Device selectById(UUID id)
    {
        log.info("Getting device for: " + id);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            Device device = (Device) session.get(Device.class, id);
            txn.commit();

            return device;
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

    public void insert(Device device)
    {
        log.info("New device for " + device.getDeviceId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(device);
            txn.commit();
        }
        catch (Exception e)
        {
            if (txn != null)
            {
                txn.rollback();
            }
            e.printStackTrace();
        }
        finally
        {
            if (session != null)
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
            try {
                txn.begin();
                Query q1 = session.createSQLQuery("DELETE FROM DEVICES");
                q1.executeUpdate();
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
