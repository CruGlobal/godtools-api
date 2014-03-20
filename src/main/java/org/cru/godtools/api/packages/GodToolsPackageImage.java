package org.cru.godtools.api.packages;

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
