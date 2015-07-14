package org.cru.godtools.domain.images;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
@Entity
@Table(name="referenced_images")
public class ReferencedImage
{
	@Id
	private ReferencedImageKey id;

	@Column(insertable = false, updatable = false)
	private UUID imageId;
	@Column(insertable = false, updatable = false)
	private UUID packageStructureId;

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

	public ReferencedImageKey getId()
	{
		return id;
	}

	public void setId(ReferencedImageKey id)
	{
		this.id = id;
	}
}
