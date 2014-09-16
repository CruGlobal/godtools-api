package org.cru.godtools.domain.images;

import com.google.common.collect.Sets;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

	public List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId, boolean filter)
	{
		List<ReferencedImage> referencedImages = selectByPackageStructureId(packageStructureId);

		if(filter)
		{
			pareDownListToOneRowPerImageId(referencedImages);
		}

		return referencedImages;
	}

	private void pareDownListToOneRowPerImageId(List<ReferencedImage> referencedImages)
	{
		Set<UUID> foundIds = Sets.newHashSet();
		Iterator<ReferencedImage> i = referencedImages.iterator();
		for( ; i.hasNext(); )
		{
			ReferencedImage nextReferencedImage = i.next();

			if(foundIds.contains(nextReferencedImage.getImageId()))
			{
				i.remove();
			}
			else
			{
				foundIds.add(nextReferencedImage.getImageId());
			}
		}
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
