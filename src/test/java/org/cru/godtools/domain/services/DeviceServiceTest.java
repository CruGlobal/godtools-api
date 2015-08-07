package org.cru.godtools.domain.services;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.utils.*;
import org.cru.godtools.utils.collections.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.*;
import org.junit.runner.*;
import org.testng.Assert;

import javax.inject.Inject;
import javax.transaction.*;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
@RunWith(Arquillian.class)
public class DeviceServiceTest
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

	@Inject
	DeviceService deviceService;

	@Inject
	UserTransaction userTransaction;

	UUID id = UUID.randomUUID();

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@Before
	public void setup() throws SystemException, NotSupportedException
	{
		userTransaction.begin();
		deviceService.insert(createNotificationRegistration(id));
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

	@Test
	public void testInsertNotification()
	{
		Device returnedDevice = deviceService.selectById(id);
		Assert.assertNotNull(returnedDevice);

	}

	private Device createNotificationRegistration(UUID id)
	{
		Device device = new Device();
		device.setId(id);
		device.setDeviceId("Device");
		device.setRegistrationId("Registration");

		return device;
	}
}
