package org.cru.godtools.domain.images;

import org.testng.Assert;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class ImageServiceTestMockData
{
	public static void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(ImageServiceTest.TEST_IMAGE_ID);
		image.setResolution("Medium");
		image.setImageContent("aasdfsdf".getBytes());

		imageService.insert(image);
	}

	public static void modifyImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(ImageServiceTest.TEST_IMAGE_ID);
		image.setImageContent("sdfasdfasd".getBytes());
		image.setResolution("Medium");

		imageService.update(image);
	}

	public static void persistRetinaImage(ImageService imageService)
	{
		Image retinaImage = new Image();
		retinaImage.setId(ImageServiceTest.TEST_RETINA_IMAGE_ID);
		retinaImage.setResolution("High");
		retinaImage.setImageContent("afasfass".getBytes());

		imageService.insert(retinaImage);
	}

	public static void validateImage(Image image)
	{
		Assert.assertNotNull(image);
		Assert.assertEquals(image.getId(), ImageServiceTest.TEST_IMAGE_ID);
		Assert.assertEquals(image.getResolution(), "Medium");
		Assert.assertEquals(image.getImageContent(), "aasdfsdf".getBytes());
	}

	public static void validateModifiedImage(Image modifiedImage)
	{
		Assert.assertNotNull(modifiedImage);
		Assert.assertEquals(modifiedImage.getId(), ImageServiceTest.TEST_IMAGE_ID);
		Assert.assertEquals(modifiedImage.getResolution(), "Medium");
		Assert.assertEquals(modifiedImage.getImageContent(), "sdfasdfasd".getBytes());
	}
}
