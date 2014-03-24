package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class ImagePageRelationshipService
{
    Connection sqlConnection;

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

    public static class ImagePageRelationshipQueries
    {
        public static final String insert = "INSERT into page_images(id, page_id, image_id) VALUES(:id, :pageId, :imageId)";
    }
}
