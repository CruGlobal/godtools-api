package org.cru.godtools.domain.images;

import javax.persistence.*;
import java.io.*;
import java.util.*;

/**
 * Created by justinsturm on 7/14/15.
 */
@Embeddable
public class ReferencedImageKey implements Serializable
{
    private UUID imageId;
    private UUID packageStructureId;

    @Column(name="image_id")
    public UUID getImageId()
    {
        return imageId;
    }

    public void setImageId(UUID imageId)
    {
        this.imageId = imageId;
    }

    @Column(name="package_structure_id")
    public UUID getPackageStructureId()
    {
        return packageStructureId;
    }

    public void setPackageStructureId(UUID packageStructureId)
    {
        this.packageStructureId = packageStructureId;
    }
}
