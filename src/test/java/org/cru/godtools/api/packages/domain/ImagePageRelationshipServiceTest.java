package org.cru.godtools.api.packages.domain;

import org.cru.godtools.tests.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class ImagePageRelationshipServiceTest extends AbstractServiceTest
{
	private ImagePageRelationshipServiceTestMockDataService mockData;

	private ImagePageRelationshipService imagePageRelationshipService;

	public static final UUID TEST_PAGE_ID = UUID.randomUUID();
	public static final UUID TEST_IMAGE_ID = UUID.randomUUID();
	public static final UUID TEST_IMAGE_PAGE_RELATIONSHIP_ID = UUID.randomUUID();

	@BeforeClass
	@Override
	public void setup()
	{
		super.setup();

		imagePageRelationshipService = new ImagePageRelationshipService(sqlConnection);

		mockData = new ImagePageRelationshipServiceTestMockDataService();
		mockData.persistPage(new PageService(sqlConnection));
		mockData.persistImage(new ImageService(sqlConnection, imagePageRelationshipService));
		mockData.persistImagePageRelationship(imagePageRelationshipService);
	}

	@Test
	public void testSelectByPageId()
	{
		List<ImagePageRelationship> imagePageRelationships = imagePageRelationshipService.selectByPageId(TEST_PAGE_ID, PixelDensity.getEnum("High"));

		Assert.assertEquals(imagePageRelationships.size(), 1);
		mockData.validateImagePageRelationship(imagePageRelationships.get(0));
	}

}
