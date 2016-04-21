package org.cru.godtools.domain.images;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "referencedImages")
public class ReferencedImage
{
	@XmlElement
	private UUID imageId;
	@XmlElement
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
}
