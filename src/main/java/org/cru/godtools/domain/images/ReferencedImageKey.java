package org.cru.godtools.domain.images;

import org.cru.godtools.domain.packages.*;
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
    @ManyToOne
    @JoinColumn(name="image_id")
    private Image image;
    @ManyToOne
    @JoinColumn(name="package_structure_id")
    private PackageStructure packageStructure;

    public ReferencedImageKey() {}

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    public PackageStructure getPackageStructure() { return packageStructure; }
    public void setPackageStructure(PackageStructure packageStructure) { this.packageStructure = packageStructure; }
}
