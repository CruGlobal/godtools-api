package org.cru.godtools.domain.services.JPAStandard;

import com.google.common.collect.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/14/15.
 */
@JPAStandard
public class JPAReferencedImageService implements ReferencedImageService
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

    public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId)
    {
        log.info("Selecting by Referenced Image with Package Structure Id " + packageStructureId);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            List referencedImages = session.createQuery("FROM ReferencedImage WHERE id.packageStructure.id = :packageStructureId")
                    .setEntity("packageStructureId",packageStructureId)
                    .list();
            txn.commit();

            return referencedImages;
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
        finally {
            if(session!=null)
            {
                session.close();
            }
        }
    }

    public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId, boolean filter)
    {
        log.info("Selecting by Referenced Image with Package Structure Id " + packageStructureId + " with filter:" + filter);

        List referencedImages = selectByPackageStructureId(packageStructureId);

        if(filter)
        {
            pareDownListToOneRowPerImageId(referencedImages);
        }

        return referencedImages;
    }

    private void pareDownListToOneRowPerImageId(List<ReferencedImage> referencedImages)
    {
        Set<UUID> foundIds = Sets.newHashSet();
        Iterator<ReferencedImage> i = referencedImages.iterator();
        for( ; i.hasNext(); )
        {
            ReferencedImage nextReferencedImage = i.next();

            if(foundIds.contains(nextReferencedImage.getImage() != null ? nextReferencedImage.getImage().getId() : null))
            {
                i.remove();
            }
            else
            {
                foundIds.add(nextReferencedImage.getImage() != null ? nextReferencedImage.getImage().getId() : null);
            }
        }
    }

    public void insert(ReferencedImage referencedImage)
    {
        log.info("Inserting Referenced Image with Package Structure Id " + referencedImage.getPackageStructure().getId()
            + " and Image Id " + referencedImage.getImage().getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(referencedImage);
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



                Query q1 = session.createQuery("DELETE FROM ReferencedImage");
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