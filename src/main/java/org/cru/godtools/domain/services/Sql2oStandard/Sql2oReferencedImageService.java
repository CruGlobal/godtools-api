package org.cru.godtools.domain.services.Sql2oStandard;

import com.google.common.collect.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.sql2o.Connection;

import javax.enterprise.inject.*;
import javax.inject.*;
import java.sql.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
@Default
public class Sql2oReferencedImageService implements ReferencedImageService
{
    Connection sqlConnection;

    @Inject
    public Sql2oReferencedImageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId)
    {
        return sqlConnection.createQuery(ReferencedImageQueries.selectByPackageStructureId)
                .setAutoDeriveColumnNames(true)
                .addParameter("packageStructureId", packageStructureId)
                .executeAndFetch(ReferencedImage.class);
    }

    public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId, boolean filter)
    {
        List<ReferencedImage> referencedImages = selectByPackageStructureId(packageStructureId);

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
        sqlConnection.createQuery(ReferencedImageQueries.insert)
                .addParameter("imageId", referencedImage.getImage() != null ? referencedImage.getImage().getId() : null)
                .addParameter("packageStructureId", referencedImage.getPackageStructure() != null ? referencedImage.getPackageStructure().getId() : null)
                .executeUpdate();
    }

    public static class ReferencedImageQueries
    {
        public static String insert = "INSERT into referenced_images(image_id, package_structure_id) VALUES(:imageId, :packageStructureId)";
        public static String selectByPackageStructureId = "SELECT * FROM referenced_images WHERE package_structure_id = :packageStructureId";
    }

    public void setAutoCommit(boolean autoCommit)
    {
        try
        {
            sqlConnection.getJdbcConnection().setAutoCommit(autoCommit);
        }
        catch(SQLException e)
        {
            /*Do Nothing*/
        }
    }

    public void rollback()
    {
        try
        {
            sqlConnection.getJdbcConnection().rollback();
        }
        catch(SQLException e)
        {
            /*Do Nothing*/
        }
    }
}
