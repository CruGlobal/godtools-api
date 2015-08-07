package org.cru.godtools.domain.services;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.mockdata.*;
import org.cru.godtools.utils.collections.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.*;
import org.junit.runner.*;

import javax.inject.Inject;
import javax.transaction.*;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/1/14.
 */
@RunWith(Arquillian.class)
public class ImageServiceTest
{
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

	public static final UUID TEST_IMAGE_ID = UUID.randomUUID();
	public static final UUID TEST_RETINA_IMAGE_ID = UUID.randomUUID();

	@Inject
	ImageService imageService;

	@Inject
	UserTransaction userTransaction;

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@Before
	public void setup() throws SystemException, NotSupportedException
	{
		userTransaction.begin();

		ImageMockData.persistImage(imageService);
		ImageMockData.persistRetinaImage(imageService);
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
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
