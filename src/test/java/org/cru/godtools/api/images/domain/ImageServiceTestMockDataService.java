package org.cru.godtools.api.images.domain;

import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageService;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.testng.Assert;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class ImageServiceTestMockDataService
{
	public void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(ImageServiceTest.TEST_IMAGE_ID);
		image.setResolution("Medium");
		image.setImageContent("aasdfsdf".getBytes());

		imageService.insert(image);
	}

	public void modifyImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(ImageServiceTest.TEST_IMAGE_ID);
		image.setImageContent("sdfasdfasd".getBytes());
		image.setResolution("Medium");

		imageService.update(image);
	}

	public void persistRetinaImage(ImageService imageService)
	{
		Image retinaImage = new Image();
		retinaImage.setId(ImageServiceTest.TEST_RETINA_IMAGE_ID);
		retinaImage.setImageHash("abcd4324");
		retinaImage.setResolution("High");
		retinaImage.setImageContent("afasfass".getBytes());

		imageService.insert(retinaImage);
	}

	public void persistPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(ImageServiceTest.TEST_PAGE_ID);

		pageService.insert(page);
	}

	public void validateImage(Image image)
	{
		Assert.assertNotNull(image);
		Assert.assertEquals(image.getId(), ImageServiceTest.TEST_IMAGE_ID);
		Assert.assertEquals(image.getImageHash(), ShaGenerator.calculateHash("aasdfsdf".getBytes()));
		Assert.assertEquals(image.getResolution(), "Medium");
		Assert.assertEquals(image.getImageContent(), "aasdfsdf".getBytes());
	}

	public void validateModifiedImage(Image modifiedImage)
	{
		Assert.assertNotNull(modifiedImage);
		Assert.assertEquals(modifiedImage.getId(), ImageServiceTest.TEST_IMAGE_ID);
		Assert.assertEquals(modifiedImage.getImageHash(), ShaGenerator.calculateHash("sdfasdfasd".getBytes()));
		Assert.assertEquals(modifiedImage.getResolution(), "Medium");
		Assert.assertEquals(modifiedImage.getImageContent(), "sdfasdfasd".getBytes());
	}
}
