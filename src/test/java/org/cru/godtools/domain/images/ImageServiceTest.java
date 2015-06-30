package org.cru.godtools.domain.images;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.services.*;
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
import java.sql.SQLException;
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
		imageService.setAutoCommit(false);
		ImageServiceTestMockData.persistImage(imageService);
		ImageServiceTestMockData.persistRetinaImage(imageService);
	}

	@AfterMethod
	public void cleanup()
	{
		imageService.rollback();
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
