package org.cru.godtools.domain.images;

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
}
