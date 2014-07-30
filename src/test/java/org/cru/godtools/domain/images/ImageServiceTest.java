package org.cru.godtools.domain.images;

import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.packages.PackageServiceTestMockData;
import org.sql2o.Connection;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class ImageServiceTest extends AbstractServiceTest
{
	public static final UUID TEST_IMAGE_ID = UUID.randomUUID();
	public static final UUID TEST_RETINA_IMAGE_ID = UUID.randomUUID();

	@Inject
	ImageService imageService;

	@BeforeMethod
	public void setup()
	{
		try
		{
			imageService.sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		ImageServiceTestMockData.persistImage(imageService);
		ImageServiceTestMockData.persistRetinaImage(imageService);
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			imageService.sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
	}

	@Test
	public void testSelectById()
	{
		Image image = imageService.selectById(TEST_IMAGE_ID);

		ImageServiceTestMockData.validateImage(image);
	}

	@Test
	public void testUpdate() throws Exception
	{
		ImageServiceTestMockData.modifyImage(imageService);
		ImageServiceTestMockData.validateModifiedImage(imageService.selectById(TEST_IMAGE_ID));
	}

}
