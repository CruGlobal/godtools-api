package org.cru.godtools.api.images.domain;

import org.cru.godtools.tests.AbstractServiceTest;
import org.sql2o.Connection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

	@BeforeClass()
	public void setup()
	{
		super.setup();

		imageService = new ImageService(sqlConnection, new ReferencedImageService(sqlConnection));

		mockData = new ImageServiceTestMockDataService();

		mockData.persistImage(imageService);
		mockData.persistRetinaImage(imageService);
	}

	@Test
	public void testSelectById()
	{
		Image image = imageService.selectById(TEST_IMAGE_ID);

		mockData.validateImage(image);
	}

	@Test
	public void testUpdate() throws Exception
	{
		Connection nonAutoCommitSqlConnection1 = sqlConnection.getSql2o().beginTransaction();

		try
		{
			ImageService nonAutoCommitImageService = new ImageService(sqlConnection, new ReferencedImageService(nonAutoCommitSqlConnection1));

			mockData.modifyImage(nonAutoCommitImageService);
			mockData.validateModifiedImage(nonAutoCommitImageService.selectById(TEST_IMAGE_ID));
		}

		finally
		{
			nonAutoCommitSqlConnection1.rollback();
		}

	}

}
