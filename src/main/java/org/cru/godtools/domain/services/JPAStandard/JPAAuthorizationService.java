package org.cru.godtools.domain.services.JPAStandard;

import com.google.common.base.*;
import org.cru.godtools.domain.services.annotations.JPAStandard;
import org.cru.godtools.domain.authentication.*;
import org.cru.godtools.domain.services.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.*;

/**
 * Created by justinsturm on 6/29/15.
 */
@JPAStandard
public class JPAAuthorizationService implements AuthorizationService
{

    private static final SessionFactory sessionFactory = buildSessionFactory();

    Logger log = Logger.getLogger(JPAAuthorizationService.class);

    private static final SessionFactory buildSessionFactory()
    {
        try
        {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            return configuration.buildSessionFactory( new StandardServiceRegistryBuilder().applySettings( configuration.getProperties()).build());
        }
        catch( Throwable ex )
        {
            System.err.println("Initial SessionFactory creation failed");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Optional<AuthorizationRecord> getAuthorizationRecord(String authTokenParam, String authTokenHeader)
    {
        String authToken = authTokenHeader == null ? authTokenParam : authTokenHeader;
        log.info("Getting authorization for: " + authToken);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            AuthorizationRecord authorizationRecord = (AuthorizationRecord) session.get(AuthorizationRecord.class, authToken);
            txn.commit();

            return Optional.fromNullable(authorizationRecord);
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

    public void recordNewAuthorization(AuthorizationRecord authenticationRecord)
    {
        log.info("New authorization for " + authenticationRecord.getUsername());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(authenticationRecord);
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
            if (session != null)
            {
                session.close();
            }
        }
    }

    public AccessCodeRecord getAccessCode(String accessCode)
    {
        log.info("Getting accessCode for: " + accessCode);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            AccessCodeRecord accessCodeRecord = (AccessCodeRecord) session.get(AccessCodeRecord.class, accessCode);
            txn.commit();

            return accessCodeRecord;
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

    public void setAutoCommit(boolean autoCommit)
    {
        /*Do Nothing*/
    }

    public void rollback()
    {
        /*Do Nothing*/
    }
}
