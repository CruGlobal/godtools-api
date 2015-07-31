package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.cru.godtools.domain.translations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/13/15.
 */
@JPAStandard
public class JPAPackageService implements PackageService
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

    public Package selectById(UUID id)
    {
        log.info("Selecting Package with Id " + id);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            Package returnPackage = (Package) session.get(Package.class, id);
            txn.commit();

            return returnPackage;
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

    public Package selectByCode(String code)
    {
        log.info("Selecting Package with Code " + code);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            Package returnPackage = (Package) session.createQuery("FROM Package WHERE code = :code")
                    .setString("code",code)
                    .uniqueResult();
            txn.commit();

            return returnPackage;
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

    public List<Package> selectAllPackages()
    {
        log.info("Selecting All Packages");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List packages = session.createQuery("FROM Package")
                    .list();
            txn.commit();

            return packages;
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

    public Package selectByOneskyProjectId(Integer translationProjectId)
    {
        log.info("Selecting Package with Project Id " + translationProjectId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            Package returnPackage = (Package) session.createQuery("FROM Package WHERE translationProjectId = :projectId")
                    .setInteger("projectId",translationProjectId)
                    .uniqueResult();
            txn.commit();

            return returnPackage;
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

    public void insert(Package godToolsPackage)
    {
        log.info("Inserting Package with Id " + godToolsPackage.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(godToolsPackage);
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

                Package persistentPackage;
                List<Package> packages = selectAllPackages();

                for(Package gtPackage : packages)
                {
                    List<PackageStructure> packageStructures = session.createQuery("FROM PackageStructure WHERE gtPackage.id = :packageId")
                            .setParameter("packageId",gtPackage.getId())
                            .list();

                    List<Translation> translations = session.createQuery("FROM Translation WHERE gtPackage.id = :packageId")
                            .setParameter("packageId", gtPackage.getId())
                            .list();

                    for(PackageStructure packageStructure : packageStructures)
                    {
                        packageStructure.setPackage(null);
                        session.update(packageStructure);
                    }

                    for(Translation translation : translations)
                    {
                        translation.setPackage(null);
                        session.update(translation);
                    }

                    persistentPackage = (Package) session.load(Package.class, gtPackage.getId());
                    session.delete(persistentPackage);
                }

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
