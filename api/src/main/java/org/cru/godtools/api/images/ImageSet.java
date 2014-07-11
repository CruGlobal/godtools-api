package org.cru.godtools.api.images;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageLookup;
import org.cru.godtools.domain.images.ImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class ImageSet
{

	Set<String> imageFileNameSet;
	Set<Image> imageSet;

	public ImageSet(Set<String> referencedImageOriginalFilenames)
	{
		imageFileNameSet = referencedImageOriginalFilenames;
	}

	public void saveImages(ImageService imageService, ImageLookup imageLookup)
	{
		imageSet = Sets.newHashSet();
		for(String filename : imageFileNameSet)
		{
			BufferedImage physicalImage = imageLookup.findByFilename(filename);

			Image databaseImage = checkForPhysicalImageAlreadyInDatabase(physicalImage, imageService);

			if(databaseImage != null)
			{
				imageSet.add(databaseImage);
			}
			else
			{
				Image newImage = new Image();
				newImage.setId(UUID.randomUUID());
				newImage.setImageContent(bufferedImageToByteArray(physicalImage));
				newImage.setResolution("High");

				imageService.insert(newImage);

				imageSet.add(newImage);
			}
		}
	}

	private Image checkForPhysicalImageAlreadyInDatabase(BufferedImage physicalImage, ImageService imageService)
	{
		return null;
	}

	private byte[] bufferedImageToByteArray(BufferedImage bufferedImage)
	{
		try
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
			byteArrayOutputStream.flush();
			byte[] imageBytes = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();

			return imageBytes;
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}
}
