package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
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

    public Image selectByFilename(String filename)
    {
        return sqlConnection.createQuery(ImageQueries.selectByFilename)
                .setAutoDeriveColumnNames(true)
                .addParameter("filename", filename)
                .executeAndFetchFirst(Image.class);
    }
    public void insert(Image image)
    {
        sqlConnection.createQuery(ImageQueries.insert)
                .addParameter("id", image.getId())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("filename", image.getFilename())
                .addParameter("imageHash", image.getImageHash())
                .executeUpdate();
    }

    public static class ImageQueries
    {
        public static final String selectById = "SELECT * FROM images where id = :id";
        public static final String selectByFilename = "SELECT * FROM images where filename = :filename";
        public static final String insert = "INSERT INTO images(id, image_content, filename, image_hash) VALUES(:id, :imageContent, :filename, :imageHash)";
    }
}
