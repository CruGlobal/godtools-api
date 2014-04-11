package org.cru.godtools.api.packages.domain;

import com.google.common.collect.Sets;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class ImageService
{
    Connection sqlConnection;

    @Inject
    public ImageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public Image selectById(UUID id)
    {
        return sqlConnection.createQuery(ImageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Image.class);
    }

    public void update(Image image)
    {
        sqlConnection.createQuery(ImageQueries.update)
                .addParameter("id", image.getId())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("imageHash", image.getImageHash())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public void insert(Image image)
    {
        sqlConnection.createQuery(ImageQueries.insert)
                .addParameter("id", image.getId())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("imageHash", image.getImageHash())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public static class ImageQueries
    {
        public static final String selectById = "SELECT * FROM images where id = :id";
        public static final String insert = "INSERT INTO images(id, image_content, image_hash, resolution) VALUES(:id, :packageId, :imageContent, :imageHash, :resolution)";
        public static final String update = "UPDATE images SET image_content = :imageContent, image_hash = :imageHash, resolution = :resolution WHERE id = :id";
    }
}
