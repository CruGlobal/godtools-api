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
public class JPANotificationService implements NotificationService{

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

    public List<Notification> selectAllUnsentNotifications()
    {
        log.info("Getting all unsent notifications");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List notifications = session.createQuery("FROM Notification WHERE notificationSent = false").list();
            txn.commit();

            return notifications;
        }
        catch(Exception e)
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
            if(session != null)
            {
                session.close();
            }
        }
    }

    public Notification selectNotificationByRegistrationIdAndType(Notification notification)
    {
        log.info("Getting notification for registration " + notification.getRegistrationId() + " of type" + notification.getNotificationType());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();
        try
        {
            txn.begin();
            Notification returnNotification = (Notification) session.createQuery("FROM Notification WHERE registrationId = :rId AND notificationType = :nType")
                    .setString("rId", notification.getRegistrationId()).setInteger("nType", notification.getNotificationType()).uniqueResult();
            txn.commit();

            return returnNotification;
        }
        catch(Exception e)
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
            if(session != null)
            {
                session.close();
            }
        }
    }

    public void updateNotification(Notification notification)
    {
        log.info("Updating notification id: " + notification.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.update(notification);
            txn.commit();
        }
        catch(Exception e)
        {
            if (txn != null)
            {
                txn.rollback();
            }
            e.printStackTrace();
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
    }

    public void insertNotification(Notification notification)
    {
        log.info("Inserting notification id: " + notification.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(notification);
            txn.commit();
        }
        catch(Exception e)
        {
            if (txn != null)
            {
                txn.rollback();
            }
            e.printStackTrace();
        }
        finally
        {
            if(session != null)
            {
                session.close();
            }
        }
    }

    public void setNotificationAsSent(UUID id)
    {
        log.info("Setting notification with id:" + id + "to sent");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            Notification notification = (Notification) session.get(Notification.class, id);
            notification.setNotificationSent(true);
            session.update(notification);
            txn.commit();
        }
        catch(Exception e)
        {
            if (txn != null)
            {
                txn.rollback();
            }
            e.printStackTrace();
        }
        finally
        {
            if(session != null)
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

                Notification persistentNotification;
                List<Notification> notifications = session.createQuery("FROM Notification").list();

                for(Notification notification : notifications)
                {
                    persistentNotification = (Notification) session.load(Notification.class, notification.getId());
                    session.delete(persistentNotification);
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
