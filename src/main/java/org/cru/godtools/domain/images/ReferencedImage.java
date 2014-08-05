package org.cru.godtools.domain.images;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class ReferencedImage
{
	private UUID imageId;
	private UUID packageStructureId;
	private String density;

	public UUID getImageId()
	{
		return imageId;
	}

	public void setImageId(UUID imageId)
	{
		this.imageId = imageId;
	}

	public UUID getPackageStructureId()
	{
		return packageStructureId;
	}

	public void setPackageStructureId(UUID packageStructureId)
	{
		this.packageStructureId = packageStructureId;
	}

	public String getDensity()
	{
		return density;
	}

	public void setDensity(String density)
	{
		this.density = density;
	}
}
