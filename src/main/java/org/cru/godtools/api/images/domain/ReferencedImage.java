package org.cru.godtools.api.images.domain;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class ReferencedImage
{
	private UUID imageId;
	private UUID pageId;
	private UUID versionId;

	public UUID getImageId()
	{
		return imageId;
	}

	public void setImageId(UUID imageId)
	{
		this.imageId = imageId;
	}

	public UUID getPageId()
	{
		return pageId;
	}

	public void setPageId(UUID pageId)
	{
		this.pageId = pageId;
	}

	public UUID getVersionId()
	{
		return versionId;
	}

	public void setVersionId(UUID versionId)
	{
		this.versionId = versionId;
	}
}
