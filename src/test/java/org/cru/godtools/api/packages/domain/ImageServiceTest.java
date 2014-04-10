package org.cru.godtools.api.packages.domain;

import com.google.common.collect.Lists;
import org.cru.godtools.tests.AbstractServiceTest;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class ImageServiceTest extends AbstractServiceTest
{
	ImageServiceTestMockDataService mockData;

	ImageService imageService;

	public static final UUID TEST_IMAGE_ID = UUID.randomUUID();
	public static final UUID TEST_RETINA_IMAGE_ID = UUID.randomUUID();
	public static final UUID TEST_PAGE_ID = UUID.randomUUID();

	@BeforeClass()
	public void setup()
	{
		super.setup();

		imageService = new ImageService(sqlConnection);

		mockData = new ImageServiceTestMockDataService();

		mockData.persistImage(imageService);
		mockData.persistRetinaImage(imageService);
		mockData.persistPage(new PageService(sqlConnection));
	}

	@Test
	public void testSelectById()
	{
		Image image = imageService.selectById(TEST_IMAGE_ID);

		mockData.validateImage(image);
	}

	@Test
	public void testSelectByFilename()
	{
		Image image = imageService.selectByFilename("image.png");

		mockData.validateImage(image);
	}

	@Test
	public void testSelectRetinaImages()
	{
		List<Image> retinaImages = imageService.selectRetinaFiles();

		Assert.assertEquals(retinaImages.size(), 1);

		mockData.validateRetinaImage(retinaImages.get(0));
	}

	@Test
	public void testUpdate() throws Exception
	{
		Connection nonAutoCommitSqlConnection1 = sqlConnection.getSql2o().beginTransaction();

		try
		{
			ImageService nonAutoCommitImageService = new ImageService(nonAutoCommitSqlConnection1);

			mockData.modifyImage(nonAutoCommitImageService);
			mockData.validateModifiedImage(nonAutoCommitImageService.selectById(TEST_IMAGE_ID));
		}

		finally
		{
			nonAutoCommitSqlConnection1.rollback();
		}

	}

}
