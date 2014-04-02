package org.cru.godtools.api.packages.domain;

import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class ImagePageRelationshipServiceTestMockDataService
{
	public void persistImagePageRelationship(ImagePageRelationshipService imagePageRelationshipService)
	{
		ImagePageRelationship imagePageRelationship = new ImagePageRelationship();
		imagePageRelationship.setId(ImagePageRelationshipServiceTest.TEST_IMAGE_PAGE_RELATIONSHIP_ID);
		imagePageRelationship.setImageId(ImagePageRelationshipServiceTest.TEST_IMAGE_ID);
		imagePageRelationship.setPageId(ImagePageRelationshipServiceTest.TEST_PAGE_ID);

		imagePageRelationshipService.insert(imagePageRelationship);
	}


	public void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(ImagePageRelationshipServiceTest.TEST_IMAGE_ID);
		image.setResolution("High");

		imageService.insert(image);
	}

	public void persistPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(ImagePageRelationshipServiceTest.TEST_PAGE_ID);

		pageService.insert(page);
	}

	public void validateImagePageRelationship(ImagePageRelationship imagePageRelationship)
	{
		Assert.assertNotNull(imagePageRelationship);
		Assert.assertEquals(imagePageRelationship.getId(), ImagePageRelationshipServiceTest.TEST_IMAGE_PAGE_RELATIONSHIP_ID);
		Assert.assertEquals(imagePageRelationship.getPageId(), ImagePageRelationshipServiceTest.TEST_PAGE_ID);
		Assert.assertEquals(imagePageRelationship.getImageId(), ImagePageRelationshipServiceTest.TEST_IMAGE_ID);
	}
}
