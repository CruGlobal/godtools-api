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


	public void insert(ReferencedImage referencedImage)
	{
		sqlConnection.createQuery(ReferencedImageQueries.insert)
				.addParameter("imageId", referencedImage.getImageId())
				.addParameter("packageStructureId", referencedImage.getPackageStructureId())
				.executeUpdate();
	}

	public static class ReferencedImageQueries
	{
		public static String insert = "INSERT into referenced_images(image_id, package_structure_id) VALUES(:imageId, :packageStructureId)";
		public static String selectByPackageStructureId = "SELECT * FROM referenced_images WHERE package_structure_id = :packageStructureId";
	}
}
