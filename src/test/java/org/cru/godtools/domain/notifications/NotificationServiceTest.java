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
import java.util.List;
import java.util.UUID;

/**
 * Created by matthewfrederick on 1/5/15.
 */
public class NotificationServiceTest extends Arquillian
{
	@Inject
	NotificationService notificationService;

	UUID id = UUID.randomUUID();

	@Deployment
	public static JavaArchive createDeployment()
	{
		Sql2oTestClassCollection sql2oTestClassCollection = new Sql2oTestClassCollection();

		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(sql2oTestClassCollection.getClasses())
				.addClasses(NotificationService.class, ClockImpl.class)
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
			notificationService.sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		notificationService.insertNotification(createNotification(id));

	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			notificationService.sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
	}

	@Test
	public void testInsertAndUpdateNotification()
	{
		Notification notification = new Notification();
		notification.setRegistrationId("Test");
		notification.setNotificationType(2);
		Notification returnedNotification = notificationService.selectNotificationByRegistrationIdAndType(notification);
		Assert.assertNotNull(returnedNotification);

		returnedNotification.setNotificationSent(true);
		notificationService.updateNotification(returnedNotification);
		notification = notificationService.selectNotificationByRegistrationIdAndType(notification);
		Assert.assertEquals(notification.isNotificationSent(), true);
	}

	@Test
	public void testCountRegIds()
	{
		Integer count = notificationService.countRegistrationIds();
		Assert.assertEquals(count, new Integer(1));
	}

	@Test
	public void testGetAllRegIds()
	{
		List<String> regIds = notificationService.getAllRegistrationIds(0);
		Assert.assertEquals(regIds.size(), 1);
	}

	private Notification createNotification(UUID id)
	{
		Notification notification = new Notification();
		notification.setId(id);
		notification.setRegistrationId("Test");
		notification.setPresentations(1);
		notification.setNotificationSent(false);
		notification.setNotificationType(2);

		return notification;
	}
}
