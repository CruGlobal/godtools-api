package org.cru.godtools.domain.notifications;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.tests.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by matthewfrederick on 12/30/14.
 */
public class DeviceServiceTest extends Arquillian
{
	@Inject
	DeviceService deviceService;

	UUID id = UUID.randomUUID();

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
		deviceService.setAutoCommit(false);

		deviceService.insert(createNotificationRegistration(id));
	}

	@AfterMethod
	public void cleanup()
	{
		deviceService.rollback();
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
