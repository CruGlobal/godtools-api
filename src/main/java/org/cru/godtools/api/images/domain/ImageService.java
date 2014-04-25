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
    Connection sqlConnection;
	ReferencedImageService referencedImageService;

    @Inject
    public ImageService(Connection sqlConnection, ReferencedImageService referencedImageService)
    {
        this.sqlConnection = sqlConnection;
		this.referencedImageService = referencedImageService;
    }

    public Image selectById(UUID id)
    {
        return sqlConnection.createQuery(ImageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Image.class);
    }

	public List<Image> selectByPageId(UUID pageId)
	{
		List<Image> images = Lists.newArrayList();
		for(ReferencedImage referencedImage : referencedImageService.selectByPageId(pageId))
		{
			images.add(selectById(referencedImage.getImageId()));
		}

		return images;
	}

	public List<Image> selectyByVersionId(UUID versionId)
	{
		List<Image> images = Lists.newArrayList();
		for(ReferencedImage referencedImage : referencedImageService.selectByVersionId(versionId))
		{
			images.add(selectById(referencedImage.getImageId()));
		}

		return images;
	}

	public Image selectByHash(String imageHash)
	{
		return sqlConnection.createQuery(ImageQueries.selectByHash)
				.setAutoDeriveColumnNames(true)
				.addParameter("imageHash", imageHash)
				.executeAndFetchFirst(Image.class);
	}

    public void update(Image image)
    {
		image.calculateHash();

        sqlConnection.createQuery(ImageQueries.update)
                .addParameter("id", image.getId())
                .addParameter("imageContent", image.getImageContent())
                .addParameter("imageHash", image.getImageHash())
                .addParameter("resolution", image.getResolution())
                .executeUpdate();
    }

    public void insert(Image image)
    {
		image.calculateHash();

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
		public static final String selectByHash = "SELECT * FROM images WHERE image_hash = :imageHash";
        public static final String insert = "INSERT INTO images(id, image_content, image_hash, resolution) VALUES(:id, :imageContent, :imageHash, :resolution)";
        public static final String update = "UPDATE images SET image_content = :imageContent, image_hash = :imageHash, resolution = :resolution WHERE id = :id";
	}
}
