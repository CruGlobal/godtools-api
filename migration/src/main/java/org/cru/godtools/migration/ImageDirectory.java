package org.cru.godtools.migration;

import com.google.common.collect.Lists;
import org.cru.godtools.domain.images.Image;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/12/14.
 */
public class ImageDirectory
{
	private File directoryPath;

	public ImageDirectory(File path)
	{
		this.directoryPath = path;
	}

	public static ImageDirectory getSharedImagesDirectory() throws URISyntaxException
	{
		return new ImageDirectory(getSharedDirectoryMarker());
	}

	public List<Image> buildImages(String packageCode)
	{
		List<Image> imageList = Lists.newArrayList();

		for(File imageFile : directoryPath.listFiles())
		{
			Image image = new Image();

			image.setId(UUID.randomUUID());
			image.setImageContent(ImageReader.read(imageFile));
			image.setFilename(packageCode + "__" + imageFile.getName());
			image.setResolution((imageFile.getName().contains("2x") ? "High" : "Medium"));

			imageList.add(image);
		}

		return imageList;
	}

	private static File getSharedDirectoryMarker() throws URISyntaxException
	{
		URL packageFolderUrl = ImageDirectory.class.getResource(PackageDirectory.DIRECTORY_BASE + "shared");
		return new File(packageFolderUrl.toURI());
	}


}