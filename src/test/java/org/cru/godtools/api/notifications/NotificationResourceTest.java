package org.cru.godtools.api.notifications;

import junit.framework.Assert;
import org.cru.godtools.api.*;
import org.cru.godtools.domain.*;
import org.cru.godtools.tests.*;
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
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(NotificationResource.class, TestClockImpl.class)
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
		notificationResource.setAutoCommit(false);
	}

	@AfterMethod
	public void cleanup()
	{
		notificationResource.rollback();
	}

	@Test
	public void testRegisterDevice()
	{
		Response response = notificationResource.registerDevice("123", "456", null);
		Assert.assertNotNull(response);
	}
}
