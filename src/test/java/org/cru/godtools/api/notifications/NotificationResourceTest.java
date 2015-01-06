package org.cru.godtools.api.notifications;

import junit.framework.Assert;
import org.cru.godtools.api.utilities.ClockImpl;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.notifications.DeviceService;
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
import javax.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * Created by matthewfrederick on 1/5/15.
 */
public class NotificationResourceTest extends Arquillian
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(NotificationResource.class, DeviceService.class, AuthorizationService.class, NotificationService.class,
						ClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	NotificationResource notificationResource;


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
		Response response = notificationResource.registerDevice("APA91bHIz9nHNI1clH4lTQSnynCwrF_dSThdJM4o1NcMXoPs3GH7N14WqZMTSYkbQgD6W7KF5l0f5XhbyEkRiO2ZOW_QcPLqMtPtXFUtPC7C8EiQiqF6EDFhxfCVqcijZL10tBfO3KuQZcvIihfriUSuWezbG5sgcu1xF18bDhBDjRdjxEXNXac", "456", null);
		Assert.assertNotNull(response);
	}
}
