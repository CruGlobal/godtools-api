package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import java.util.*;

/**
 * Created by justinsturm on 7/8/15.
 */
@JPAStandard
public class JPAImageService implements ImageService{
    private static final SessionFactory sessionFactory = buildSessionFactory();

    Logger log = Logger.getLogger(JPAImageService.class);

    private boolean autoCommit = true;

    private static final SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
            standardServiceRegistryBuilder.applySettings(configuration.getProperties());
            return configuration.buildSessionFactory(standardServiceRegistryBuilder.build());
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Image selectById(UUID id) {
        log.info("Getting image with id: " + id);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            Image image = (Image) session.get(Image.class, id);
            txn.commit();

            return image;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Image> selectAll()
    {
        log.info("Select All Images");
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            List<Image> images = session.createQuery("FROM Image").list();
            txn.commit();

            return images;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Image selectByFilename(String filename) {
        log.info("Getting image with filename: " + filename);
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            Image image = (Image) session.createQuery("FROM Image WHERE filename = :fName")
                    .setString("fName", filename).uniqueResult();
            txn.commit();

            return image;
        } catch (Exception e) {
            if (txn != null) {
                txn.rollback();
            }

            e.printStackTrace();

            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void update(Image image) {
        log.info("Updating image with id " + image.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try {
            txn.begin();
            session.update(image);
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

    public void insert(Image image)
    {
        log.info("Inserting image with id " + image.getId());
        Session session = sessionFactory.openSession();
        Transaction txn = session.getTransaction();

        try
        {
            txn.begin();
            session.save(image);
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

                Image persistentImage;
                List<Image> images = selectAll();

                for(Image image : images)
                {
                    List<ReferencedImage> referencedImages = session.createQuery("FROM ReferencedImage WHERE id.image.id = :imageId")
                            .setParameter("imageId", image.getId())
                            .list();

                    for (ReferencedImage referencedImage : referencedImages)
                    {
                        session.delete(referencedImage);
                    }

                    persistentImage = (Image) session.load(Image.class, image.getId());
                    session.delete(persistentImage);
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
