package org.cru.godtools.domain.services.JPAStandard;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 7/8/15.
 */
@JPAStandard
public class JPAImageService implements ImageService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public Image selectById(UUID id) { return entityManager.find(Image.class, id); }

    public List<Image> selectAll() { return entityManager.createQuery("FROM Image").getResultList(); }

    public Image selectByFilename(String filename) {
        return (Image) entityManager.createQuery("FROM Image WHERE filename = :fName")
                    .setParameter("fName", filename)
                    .getSingleResult();
    }

    public void update(Image image) { entityManager.merge(image); }

    public void insert(Image image) { entityManager.persist(image); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback()
    {
        clear();
    }

    private void clear()
    {
        List<Image> images = selectAll();

        for(Image image : images)
        {
            List<ReferencedImage> referencedImages = entityManager.createQuery("FROM ReferencedImage WHERE id.image.id = :imageId")
                    .setParameter("imageId", image.getId())
                    .getResultList();

            for(ReferencedImage referencedImage : referencedImages) {
                entityManager.remove(entityManager.find(ReferencedImage.class, referencedImage.getImage()));}

            entityManager.remove(entityManager.find(Image.class, image.getId()));
        }
    }
}
