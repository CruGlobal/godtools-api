package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class ImagePageRelationshipService
{
    Connection sqlConnection;

    @Inject
    public ImagePageRelationshipService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public void insert(ImagePageRelationship imagePageRelationship)
    {
        sqlConnection.createQuery(ImagePageRelationshipQueries.insert)
                .addParameter("id", imagePageRelationship.getId())
                .addParameter("pageId", imagePageRelationship.getPageId())
                .addParameter("imageId", imagePageRelationship.getImageId())
                .executeUpdate();
    }

    public List<ImagePageRelationship> selectByPageId(UUID pageId, PixelDensity pixelDensity)
    {
        return sqlConnection.createQuery(ImagePageRelationshipQueries.selectByPageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("pageId", pageId)
                .executeAndFetch(ImagePageRelationship.class);
    }

    public static class ImagePageRelationshipQueries
    {
        public static final String selectByPageId = "SELECT * FROM page_images where page_id = :pageId";
        public static final String insert = "INSERT into page_images(id, page_id, image_id) VALUES(:id, :pageId, :imageId)";
    }
}
