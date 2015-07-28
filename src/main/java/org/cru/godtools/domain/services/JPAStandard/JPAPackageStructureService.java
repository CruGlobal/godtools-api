package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.images.*;
import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.Session;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/13/15.
 */
@JPAStandard
public class JPAPackageStructureService implements PackageStructureService
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

    public void insert(PackageStructure packageStructure)
    {
        log.info("Inserting Package Structure with Id " + packageStructure.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(packageStructure);
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

    public List<PackageStructure> packageStructures()
    {
        log.info("Select All Package Structures");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List<PackageStructure> packageStructures = session.createQuery("FROM PackageStructure").list();
            txn.commit();

            return packageStructures;
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

    public PackageStructure selectByPackageId(UUID packageId)
    {
        log.info("Select Package Structure with Package Id " + packageId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            PackageStructure packageStructure = (PackageStructure) session.createQuery("FROM PackageStructure WHERE packageId = :packageId")
                    .setEntity("packageId", packageId)
                    .uniqueResult();
            txn.commit();

            return packageStructure;
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
    }

    public List<PackageStructure> selectAll()
    {
        log.info("Selecting All Package Structures");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List packageStructures = session.createQuery("FROM PackageStructure")
                    .list();
            txn.commit();

            return packageStructures;
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

                List<PackageStructure> packageStructures = selectAll();

                for(PackageStructure packageStructure : packageStructures)
                {
                    //Delete associated Referenced Image records
                    List<ReferencedImage> referencedImages = session.createQuery("FROM ReferencedImage WHERE id.packageStructureId = :packageStructureId")
                            .setParameter("packageStructureId",packageStructure.getId())
                            .list();

                    for(ReferencedImage referencedImage : referencedImages)
                    {
                        session.delete(referencedImage);
                    }

                    session.delete(packageStructure);
                }

                txn.commit();
            }
            catch (Exception e)
            {
                if(txn != null)
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
