package org.cru.godtools.api.packages.domain;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class Image
{
    private UUID id;
    private byte[] imageContent;
    private String imageHash;
    private String resolution;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

	public byte[] getImageContent()
    {
        return imageContent;
    }

    public void setImageContent(byte[] imageContent)
    {
        this.imageContent = imageContent;
    }

    public String getImageHash()
    {
        return imageHash;
    }

    public void setImageHash(String imageHash)
    {
        this.imageHash = imageHash;
    }

    public String getResolution()
    {
        return resolution;
    }

    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }
}
