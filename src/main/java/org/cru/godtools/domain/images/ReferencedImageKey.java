package org.cru.godtools.domain.images;

import org.hibernate.annotations.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

/**
 * Created by justinsturm on 7/14/15.
 */
@Embeddable
public class ReferencedImageKey implements Serializable
{
    @Type(type = "pg-uuid")
    private UUID imageId;
    @Type(type = "pg-uuid")
    private UUID packageStructureId;

    public ReferencedImageKey() {}


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
