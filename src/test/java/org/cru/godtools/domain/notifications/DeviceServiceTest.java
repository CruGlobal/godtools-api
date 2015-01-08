package org.cru.godtools.domain.notifications;

import org.cru.godtools.api.utilities.ClockImpl;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.tests.Sql2oTestClassCollection;
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
import java.sql.SQLException;
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
				.addClasses(DeviceService.class, ClockImpl.class)
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
			deviceService.sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		deviceService.insert(createNotificationRegistration(id));

	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			deviceService.sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
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
