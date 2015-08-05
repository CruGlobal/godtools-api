package org.cru.godtools.domain.services.sql2o;

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
import java.sql.*;
import java.util.UUID;

/**
 * Created by matthewfrederick on 1/5/15.
 */
public class NotificationServiceTest extends Arquillian
{
	@Inject
	NotificationService notificationService;

	@Inject
	org.sql2o.Connection sqlConnection;

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
		try
		{
			sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
				/*Do Nothing*/
		}
		notificationService.insertNotification(createNotification(id));

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
