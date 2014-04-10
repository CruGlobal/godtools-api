package org.cru.godtools.api.packages.domain;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class Image
{
    private UUID id;
	private UUID packageId;
    private byte[] imageContent;
    private String filename;
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

	public UUID getPackageId()
	{
		return packageId;
	}

	public void setPackageId(UUID packageId)
	{
		this.packageId = packageId;
	}

	public byte[] getImageContent()
    {
        return imageContent;
    }

    public void setImageContent(byte[] imageContent)
    {
        this.imageContent = imageContent;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
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
