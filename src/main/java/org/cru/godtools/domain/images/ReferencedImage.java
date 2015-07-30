package org.cru.godtools.domain.images;

import org.cru.godtools.domain.packages.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
@Entity
@Table(name="referenced_images")
public class ReferencedImage
{
	@EmbeddedId
	private ReferencedImageKey id = new ReferencedImageKey();

	public Image getImage() { return id.getImage(); }
	public void setImage(Image image) { id.setImage(image); }

	public PackageStructure getPackageStructure() { return id.getPackageStructure(); }
	public void setPackageStructure(PackageStructure packageStructure) { id.setPackageStructure(packageStructure); }

	//Deprecated, keep for SQL2O
	public void setImageId(UUID imageId)
	{
		if(id.getImage() == null)
		{
			id.setImage(new Image());
			id.getImage().setId(imageId);
		}
	}

	//Deprecated, keep for SQL2O
	public void setPackageStructureId(UUID packageStructureId)
	{
		if(id.getPackageStructure() == null)
		{
			id.setPackageStructure(new PackageStructure());
			id.getPackageStructure().setId(packageStructureId);
		}
	}
}
