package org.cru.godtools.domain.services.Sql2oStandard;

import org.cru.godtools.domain.services.ImageService;
import org.cru.godtools.domain.model.Image;
import org.sql2o.Connection;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.sql.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
@Default
public class Sql2oImageService implements ImageService
{
    @Inject
    private Connection sqlConnection;

    @Inject
    public Sql2oImageService(Connection sqlConnection)
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

    public void update(Image image)
    {
        sqlConnection.createQuery(ImageQueries.update)
                .addParameter("id", image.getId())
                .addParameter("filename", image.getFilename())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public void insert(Image image)
    {
        sqlConnection.createQuery(ImageQueries.insert)
                .addParameter("id", image.getId())
                .addParameter("filename", image.getFilename())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public static class ImageQueries
    {
        public static final String selectById = "SELECT * FROM images where id = :id";
        public static final String insert = "INSERT INTO images(id, filename, image_content, resolution) VALUES(:id, :filename, :imageContent, :resolution)";
        public static final String update = "UPDATE images SET filename = :filename, image_content = :imageContent, resolution = :resolution WHERE id = :id";
        public static final String selectByFilename = "SELECT * FROM images where filename = :filename";
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
