package org.cru.godtools.domain.images;

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

	public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId)
	{
		return sqlConnection.createQuery(ReferencedImageQueries.selectByPackageStructureId)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageStructureId", packageStructureId)
				.executeAndFetch(ReferencedImage.class);
	}

	public List<ReferencedImage> selectByPackageStructureIdAndDensity(UUID packageStructureId, String density)
	{
		return sqlConnection.createQuery(ReferencedImageQueries.selectByPackageStructureIdAndDensity)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageStructureId", packageStructureId)
				.addParameter("density", density)
				.executeAndFetch(ReferencedImage.class);
	}

	public void insert(ReferencedImage referencedImage)
	{
		sqlConnection.createQuery(ReferencedImageQueries.insert)
				.addParameter("imageId", referencedImage.getImageId())
				.addParameter("packageStructureId", referencedImage.getPackageStructureId())
				.addParameter("density", referencedImage.getDensity())
				.executeUpdate();
	}


	public static class ReferencedImageQueries
	{
		public static String insert = "INSERT into referenced_images(image_id, package_structure_id, density) VALUES(:imageId, :packageStructureId, :density)";
		public static String selectByPackageStructureId = "SELECT * FROM referenced_images WHERE package_structure_id = :packageStructureId";
		public static final String selectByPackageStructureIdAndDensity = "SELECT * FROM referenced_images WHERE package_structure_id = :packageStructureId AND density = :density";
	}
}
