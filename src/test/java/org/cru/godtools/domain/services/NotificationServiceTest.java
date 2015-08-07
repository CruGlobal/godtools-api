package org.cru.godtools.domain.services;

import org.cru.godtools.domain.*;
import org.cru.godtools.domain.model.*;
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
 * Created by matthewfrederick on 1/5/15.
 */
@RunWith(Arquillian.class)
public class NotificationServiceTest
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
	NotificationService notificationService;

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

		notificationService.insertNotification(createNotification(id));

	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
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
