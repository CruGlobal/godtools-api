package org.cru.godtools.api.images.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class ReferencedImageService
{
	Connection sqlConnection;

	@Inject
	public ReferencedImageService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	public List<ReferencedImage> selectByPageId(UUID pageId)
	{
		return sqlConnection.createQuery(ReferencedImageQueries.selectByPageId)
				.setAutoDeriveColumnNames(true)
				.addParameter("pageId", pageId)
				.executeAndFetch(ReferencedImage.class);
	}

	public List<ReferencedImage> selectByVersionId(UUID versionId)
	{
		return sqlConnection.createQuery(ReferencedImageQueries.selectByVersionId)
				.setAutoDeriveColumnNames(true)
				.addParameter("versionId", versionId)
				.executeAndFetch(ReferencedImage.class);
	}

	public void insert(ReferencedImage referencedImage)
	{
		sqlConnection.createQuery(ReferencedImageQueries.insert)
				.addParameter("imageId", referencedImage.getImageId())
				.addParameter("versionId", referencedImage.getVersionId())
				.addParameter("pageId", referencedImage.getPageId())
				.executeUpdate();
	}

	public static class ReferencedImageQueries
	{
		public static String insert = "INSERT into referenced_images(image_id, page_id, version_id) VALUES(:imageId, :pageId, :versionId)";
		public static String selectByPageId = "SELECT * FROM referenced_images WHERE page_id = :pageId";
		public static String selectByVersionId = "SELECT * FROM referenced_images WHERE version_id = :versionId";
	}
}
