package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.utils.ShaGenerator;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/10/14.
 */
public class ImageDirectory
{
	final String path;

	public ImageDirectory(String path)
	{
		this.path = path;
	}

	public static Map<String, Image> getCombinedImageByFilenameMap(ImageDirectory... imageDirectories)
	{
		Map<String, Image> masterMap = Maps.newHashMap();

		for(ImageDirectory imageDirectory : imageDirectories)
		{
			masterMap.putAll(imageDirectory.getImagesByFilenameMap());
		}

		return masterMap;
	}

	public Set<Image> getImagesSet()
	{
		File directory = getDirectory();
		Set<Image> images = Sets.newHashSet();

		for(File file : directory.listFiles())
		{
			Image image = new Image();
			image.setId(UUID.randomUUID());
			image.setImageContent(ImageReader.read(file));
			image.setImageHash(ShaGenerator.calculateHash(image.getImageContent()));
			image.setResolution("High");
			images.add(image);
		}

		return images;
	}

	public Map<String, Image> getImagesByFilenameMap()
	{
		File directory = getDirectory();
		Map<String, Image> images = Maps.newHashMap();

		for(File file : directory.listFiles())
		{
			Image image = new Image();
			image.setId(UUID.randomUUID());
			image.setImageContent(ImageReader.read(file));
			image.setImageHash(ShaGenerator.calculateHash(image.getImageContent()));
			image.setResolution("High");
			images.put(file.getName(), image);
		}

		return images;
	}

	private File getDirectory()
	{
		try
		{
			URL url = this.getClass().getResource(path);
			return new File(url.toURI());
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}

	public String getPath()
	{
		return path;
	}
}
