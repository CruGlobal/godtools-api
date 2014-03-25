package org.cru.godtools.api.packages;

import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.domain.Image;

import java.util.Set;

/**
 * Created by ryancarlson on 3/19/14.
 */
public class GodToolsPackageImage
{
    byte[] contents;
    String hash;
    String originalFilename;

    public GodToolsPackageImage(byte[] contents, String originalFilename)
    {
        this.contents = contents;
        this.originalFilename = originalFilename;
    }

    public GodToolsPackageImage(byte[] contents, String originalFilename, String imageHash)
    {
        this(contents, originalFilename);
        this.hash = imageHash;
    }

    public static Set<GodToolsPackageImage> createSet(Set<Image> databaseImages)
    {
        Set<GodToolsPackageImage> apiImages = Sets.newHashSet();

        for(Image image : databaseImages)
        {
            apiImages.add(new GodToolsPackageImage(image.getImageContent(), image.getFilename(), image.getImageHash()));
        }

        return apiImages;
    }

    public byte[] getContents()
    {
        return contents;
    }

    public void setContents(byte[] contents)
    {
        this.contents = contents;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public String getOriginalFilename()
    {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename)
    {
        this.originalFilename = originalFilename;
    }
}
