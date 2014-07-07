package org.cru.godtools.api.images.domain;

import com.google.common.collect.Lists;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.sql.Ref;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class ImageService
{
    private Connection sqlConnection;

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

	public Image selectByPackageNameAndFilename(String packageName, String filename)
	{
		return sqlConnection.createQuery(ImageQueries.selectByPackageNameAndFilename)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageName", packageName)
				.addParameter("filename", filename)
				.executeAndFetchFirst(Image.class);
	}
    public void update(Image image)
    {
        sqlConnection.createQuery(ImageQueries.update)
                .addParameter("id", image.getId())
				.addParameter("packageName", image.getPackageName())
				.addParameter("filename", image.getFilename())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public void insert(Image image)
    {
        sqlConnection.createQuery(ImageQueries.insert)
                .addParameter("id", image.getId())
				.addParameter("packageName", image.getPackageName())
				.addParameter("filename", image.getFilename())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public static class ImageQueries
    {
        public static final String selectById = "SELECT * FROM images where id = :id";
        public static final String insert = "INSERT INTO images(id, package_name, filename, image_content, resolution) VALUES(:id, :packageName, :filename, :imageContent, :resolution)";
        public static final String update = "UPDATE images SET filename = :filename, package_name = :packageName, image_content = :imageContent, resolution = :resolution WHERE id = :id";
		public static final String selectByPackageNameAndFilename = "SELECT * FROM images where filename = :filename AND package_name = :packageName";
	}
}
