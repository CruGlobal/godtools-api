package org.cru.godtools.api.notifications;

import junit.framework.Assert;
import org.cru.godtools.api.utilities.ClockImpl;
import org.cru.godtools.api.utilities.TimerControls;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.notifications.DeviceService;
import org.cru.godtools.domain.notifications.Notification;
import org.cru.godtools.domain.notifications.NotificationService;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by matthewfrederick on 1/8/15.
 */
public class NotificationPushTest extends Arquillian
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(NotificationPush.class, TimerControls.class, DeviceService.class,
						ClockImpl.class, NotificationResource.class, AuthorizationService.class, NotificationService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	NotificationResource notificationResource;
	@Inject
	NotificationService notificationService;


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
			TestSqlConnectionProducer.getConnection().getJdbcConnection().setAutoCommit(false);
		}
		catch (SQLException e)
		{
		}
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			TestSqlConnectionProducer.getConnection().getJdbcConnection().rollback();
		}
		catch (SQLException e)
		{
		}
	}

	@Test
	public void testRegisterDevice()
	{
		notificationResource.registerDevice("123", "456", null);

		Notification notification = new Notification();
		notification.setNotificationType(2);
		notification.setRegistrationId("123");

		notificationResource.updateNotification(notification, "a");

		List<Notification> notifications = notificationService.selectAllUnsentNotifications();
		Assert.assertEquals(notifications.size(), 1);

		NotificationPush push = new NotificationPush();
		push.execute();
	}
}
