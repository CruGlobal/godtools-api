package org.cru.godtools.domain.images;

import com.google.common.collect.Maps;
import org.cru.godtools.domain.GuavaHashGenerator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class Image implements Serializable
{
    private UUID id;
	private String filename;
    private byte[] imageContent;
    private String resolution;

	public static Map<String, Image> createMapOfImages(List<Image> imageList)
	{
		Map<String, Image> imageMap = Maps.newHashMap();

		for(Image image : imageList)
		{
			if(image.filename.contains("__")) image.filename = image.filename.substring(image.filename.indexOf("__") +2);
			imageMap.put(image.getFilename(), image);
			imageMap.put(GuavaHashGenerator.calculateHash(image.getImageContent()) + ".png", image);
		}

		return imageMap;
	}

	public static String buildFilename(String packageCode, String filename)
	{
		return packageCode + "__" + filename;
	}

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public byte[] getImageContent()
    {
        return imageContent;
    }

    public void setImageContent(byte[] imageContent)
    {
        this.imageContent = imageContent;
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
