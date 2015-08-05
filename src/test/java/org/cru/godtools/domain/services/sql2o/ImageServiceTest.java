package org.cru.godtools.domain.services.sql2o;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.*;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class ImageServiceTest extends Arquillian
{
	public static final UUID TEST_IMAGE_ID = UUID.randomUUID();
	public static final UUID TEST_RETINA_IMAGE_ID = UUID.randomUUID();

	@Inject
	ImageService imageService;

	@Inject
	org.sql2o.Connection sqlConnection;

	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@BeforeMethod
	public void setup()
	{
		try
		{
			sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
            /*Do Nothing*/
		}
		ImageMockData.persistImage(imageService);
		ImageMockData.persistRetinaImage(imageService);
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
				/*Do Nothing*/
		}
	}

	@Test
	public void testSelectById()
	{
		Image image = imageService.selectById(TEST_IMAGE_ID);

		ImageMockData.validateImage(image);
	}

	@Test
	public void testUpdate() throws Exception
	{
		ImageMockData.modifyImage(imageService);
		ImageMockData.validateModifiedImage(imageService.selectById(TEST_IMAGE_ID));
	}

}
