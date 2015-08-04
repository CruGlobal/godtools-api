package org.cru.godtools.domain.services.JPAStandard;

import com.google.common.collect.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;
import org.hibernate.*;
import org.hibernate.Query;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.*;
import org.jboss.logging.*;

import javax.persistence.*;
import java.util.*;

/**
 * Created by justinsturm on 7/14/15.
 */
@JPAStandard
public class JPAReferencedImageService implements ReferencedImageService
{
    @PersistenceContext(name = "gtDatasource")
    EntityManager entityManager;

    public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId)
    {
            return entityManager.createQuery("FROM ReferencedImage WHERE id.packageStructure.id = :packageStructureId")
                    .setParameter("packageStructureId",packageStructureId)
                    .getResultList();
    }

    public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId, boolean filter)
    {
        List referencedImages = selectByPackageStructureId(packageStructureId);

        if(filter)
            pareDownListToOneRowPerImageId(referencedImages);

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
                i.remove();
            else
                foundIds.add(nextReferencedImage.getImage() != null ? nextReferencedImage.getImage().getId() : null);
        }
    }

    public void insert(ReferencedImage referencedImage) { entityManager.persist(referencedImage); }

    public void setAutoCommit(boolean autoCommit) { /* Do Nothing */ }

    public void rollback() { clear(); }

    private void clear() { entityManager.createQuery("DELETE FROM ReferencedImage").executeUpdate(); }
}
