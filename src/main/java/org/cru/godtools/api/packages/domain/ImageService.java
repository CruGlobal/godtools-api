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

    public List<Image> selectRetinaFiles()
    {
        return sqlConnection.createQuery(ImageQueries.selectRetinaFiles)
                .setAutoDeriveColumnNames(true)
                .executeAndFetch(Image.class);
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

	public Set<Image> selectImagesByPageId(UUID pageId, PixelDensity pixelDensity)
	{
		Set<Image> images = Sets.newHashSet();

		return images;
	}

	public Set<Image> selectImagesForAllPages(List<Page> pages, PixelDensity pixelDensity)
	{
		Set<Image> images = Sets.newHashSet();

		for(Page page : pages)
		{
			images.addAll(selectImagesByPageId(page.getId(), pixelDensity));
		}

		return images;
	}

    public void update(Image image)
    {
        sqlConnection.createQuery(ImageQueries.update)
                .addParameter("id", image.getId())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("filename", image.getFilename())
                .addParameter("imageHash", image.getImageHash())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public void insert(Image image)
    {
        sqlConnection.createQuery(ImageQueries.insert)
                .addParameter("id", image.getId())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("filename", image.getFilename())
                .addParameter("imageHash", image.getImageHash())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public static class ImageQueries
    {
        public static final String selectById = "SELECT * FROM images where id = :id";
        public static final String selectByFilename = "SELECT * FROM images where filename = :filename";
        public static final String selectRetinaFiles = "SELECT * FROM images where filename like '%2x%'";
        public static final String insert = "INSERT INTO images(id, image_content, filename, image_hash, resolution) VALUES(:id, :imageContent, :filename, :imageHash, :resolution)";
        public static final String update = "UPDATE images SET image_content = :imageContent, filename = :filename, image_hash = :imageHash, resolution = :resolution WHERE id = :id";
    }
}
