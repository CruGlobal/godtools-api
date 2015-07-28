package org.cru.godtools.domain.images;

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
	@AttributeOverrides({
			@AttributeOverride(name="imageId", column = @Column(name="image_id")),
			@AttributeOverride(name="packageStructureId", column = @Column(name="package_structure_id"))
	})
	private ReferencedImageKey id = new ReferencedImageKey();

	@Transient
	private UUID imageId;
	@Transient
	private UUID packageStructureId;

	public UUID getImageId() {
		return imageId;
	}

	public UUID getPackageStructureId() {
		return packageStructureId;
	}

	public void setImageId(UUID imageId) {
		this.imageId = imageId;
		id.setImageId(imageId);
	}

	public void setPackageStructureId(UUID packageStructureId) {
		this.packageStructureId = packageStructureId;
		id.setPackageStructureId(packageStructureId);
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
